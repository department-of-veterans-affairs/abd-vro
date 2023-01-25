# PDF Generator

## Tools

- Python 3.9+
- [wkhtmltopdf](https://wkhtmltopdf.org/)


## Endpoints and Request Data

- [Request PDF Generation - POST http://localhost:8080/v1/evidence-pdf](http://localhost:8080/v1/evidence-pdf)
- [Fetch Generated PDF - GET http://localhost:8080/v1/evidence-pdf/{claimSubmissionID}](http://localhost:8080/v1/evidence-pdf)

Both functions work off the same endpoint. The main difference being that the `POST` has a JSON request body while the `GET` uses the URL(`claimSubmissionID`) to find the corresponding PDF.

- [Immediate PDF - GET http://localhost:8080/v1/immediate-pdf](http://localhost:8080/v1/immediate-pdf)

This endpoint is a combined version of the `generate-pdf` and`fetch-pdf`. Pass it the `POST` request body and it will respond with the PDF associated with the `claimSubmissionID`

## How it works

### Queues

On `service-python` startup, the consumer will attempt to create a `generate-pdf`, `fetch-pdf`, and `generate-fetch-pdf` queue on a `pdf-generator` exchange.

#### Generating the PDF (1/2)
Any messages passed to the `generate-pdf` queue will use the `diagnosticCode`, `pdfTemplate` and `pdfLibrary`(optional) to select the appropriate template and generate the PDF. `diagnosticCode` is used to translate this into a human readable diagnostic type based on the mapping in `config/settings.py` in the `codes` variable.

Using `pdfTemplate` like `v1` along with this diagnostic type like `hypertension` for example, it will pull up the appropriate template and template variables in the `templates` and `template_variables` folder respectively. For example, `"diagnosticCode"=7101` and `"pdfTemplate"="v1"`will fetch `templates/hypertension-v1.html` and `template_variables/hypertension-v1.json`.

The generator will first load the `hypertension-v1.json` file which is prefilled with default values for the available variables within the template and attempt to replace them with what is provided in the message. If it cannot replace the data based on what's provided in the request, it will keep the default that is defined in the JSON file.

After the HTML template is generated with the replaced variables, it used `wkhtmltopdf` by default or the library selected by `pdfLibrary` to create a PDF based on the HTML file.

#### Saving the PDF (2/2)

Once the PDF has been generated, the consumer will create a key value pair in Redis to store it due to it containing PII information. The `key` being `claimSubmissionId` while the `value` is a base64 encoded string representation of the PDF.

The consumer will return a response similar to:
```
{
  "claimSubmissionId: "1",
  "status": "COMPLETE"
}
```

#### Fetch PDF Process

When the consumer recieves a message in the `fetch-pdf` queue, it will use the provided `claimSubmissionId` to look it up on Redis.

If the PDF still hasn't been generated, you will recieve a response similar to:
```
{
  "claimSubmissionId: "1",
  "status": "IN_PROGRESS"
}
```

but if the PDF is available then the response will be a downloable file

## Adding a new Diagnostic Code

1. Edit the `codes` dictionary in `config/settings.py` by adding a new key, value pair for your code.
     - Example:
    ```
    codes = {
      "0000": "cancer",
      "0001": "diabetes" //new code with human readable diagnosis
    }
    ```
2. Create a HTML version of the PDF you want to generate and save it in the `templates` folder along with a version number
   - Take a look at [Jinja 2 Template Documentation](https://jinja.palletsprojects.com/en/3.1.x/templates/) for a better idea of what you can do within the template files
   - Every template file needs a version number. By default, the system looks for `v1` if one is not selected based from the request
   - The file name should match the name you used in Step 1. Following that example, it should be called `diabetes-v1.html`
3. Create a JSON file in `template_variables` that will contain default values for the HTML file in case they are not provided in the RabbitMQ message
    - The file name should match the name you used in Step 1 and 2. Following that example, it should be called `diabetes-v1.json`

## Helper Functions

Some diagnostic codes might need specific changes that don't need to affect other templates and instead of adding it to the assessment logic, we can use a helper function.

When generating a PDF, it will look for a helper function following this naming convention `pdf_helper_0000` where `0000` would be the code we want to use. If it does not find it, it move on and then applies a `pdf_helper_all` that gets applied to every single template. Usually these are edits like turning date string into proper date time objects, etc.

## Library Specifics (WKHTMLTOPDF / WeasyPrint)

### WKHTMLTOPDF
#### Table of Contents

ToCs are not part of the normal HTML template the gets generated fo the PDF. They need to be created through a different process and then added to the other HTML template

The PDF generator will check if there is a ToC file already created for the `diagnosticCode` that gets passed. If not found, it will generate the PDF without a ToC so you don't have to worry about having a empty section or page

#### Add a ToC for a new code:

1. Create a directory in `templates` where the name will be the human readable name used in the `codes` variable in `settings.py`
2. Within this folder, create a `toc.xsl` file. Most ToCs will follow the same format so you can just copy one from any other folder. If you needed to create one from scratch, in the commandline run the following: `wkhtmltopdf --dump-default-toc-xsl` and copy the contents of the output to a new `toc.xsl` file as stated above

#### Notes

1. By default a ToC is generated by finding all `<h>` related tags so you need to modify them if you want them ignored.
   1. To ignore a heading, it must start with `&zwnj;` like this example: `<h3 class="text-center">&zwnj;Test PDF Heading</h3>`. The `toc.xsl` file has logic in place to skip over any headings that start with this special character.
2. The ToC page is fully customizable just like any HTML page
3. Built using Webkit 2.2 (~2012) so many newer HTML features are unavailable or need to be added through other ways like custom plain CSS
4. This library renders at 96DPI but the value can be altered through the meta tags. We need to verify that the Figma design or other design software matches the proper DPI settings by making sure the resolution matches the paper size. Use the following links for proper conversions: https://a-size.com/legal-size-paper/ and https://www.papersizes.org/a-sizes-in-pixels.htm as well as https://pixelsconverter.com/inches-to-pixels


### WeasyPrint
#### Table of Contents

Work in Progress

#### Notes

1. This library does not accept Javascript. At the moment, we would need to come up with a workaround by prerendering in a secondary library or just using WKHTMLTOPDF for Javascript specific portions.
2. This library renders at 96DPI and the value cannot be changed. We need to verify that the Figma design or other design software matches the proper DPI settings by making sure the resolution matches the paper size. Use the following links for proper conversions: https://a-size.com/legal-size-paper/ and https://www.papersizes.org/a-sizes-in-pixels.htm as well as https://pixelsconverter.com/inches-to-pixels

## Testing and Development

Currently there are 2 ways to develop/test the PDF service:

1. Run `./gradlew build check docker` to start all containers and run a full test. This can be used for the testing any updates that are made to the endpoints through Swagger but it takes longer due to having to load all the other containers
2. Run `python pdfgenerator/src/lib/local_pdf_test.py` from the `service-python` directory. This file calls the PDF generator while bypassing all the related RabbitMQ and Redis code. You can alter the `diagnosis_name` and `message` to simulate an endpoint request and to quickly debug any template or PDF issues. The `diagnosis_name` should be the full name including version number like `hypertension-v1`
