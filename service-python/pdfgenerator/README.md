# PDF Generator

## Tools

- Python 3.9+
- [wkhtmltopdf](https://wkhtmltopdf.org/)


## Endpoints and Request Data

- [Request PDF Generation - POST http://localhost:8080/v1/evidence-pdf](http://localhost:8080/v1/evidence-pdf)
- [Fetch Generated PDF - GET http://localhost:8080/v1/evidence-pdf/{claimSubmissionID}](http://localhost:8080/v1/evidence-pdf)

Both functions work off the same endpoint. The main difference being that the `POST` has a JSON request body while the `GET` uses the URL(`claimSubmissionID`) to find the corresponding PDF.

## How it works

### Queues

On `service-python` startup, the consumer will attempt to create a `generate-pdf` and `fetch-pdf` queue on a `pdf-generator` exchange.

### Generate PDF Process
Any messages passed to the `generate-pdf` queue will use the `diagnosticCode` provided to translate this into a human readable diagnostic type based on the mapping in `config/settings.py` in the `codes` variable.

It will use this diagnostic type like `hypertension` for example to pull up the appropriate template and template variables in the `templates` and `template_variables` folder respectively. For example, `diagostic_type="hypertension"` will fetch `templates/hypertension.html` and `template_variables/hypertension.json`.

The generator will first load the `hypertension.json` file which is prefilled with default values for the available variables within the template and attempt to replace them with what is provided in the message. If it cannot replace the data based on what's provided in the request, it will keep the default that is defined in the JSON file.

After the HTML template is generated with the replaced variables, it used `wkhtmltopdf` to create a PDF based on the HTML file.

### Save PDF Process

Once the PDF has been generated, the consumer will create a key value pair in Redis to store it due to PII information rather than storing it in S3 or another location. The `key` being `claimSubmissionId` while the `value` is a base64 encoded string representation of the PDF.

The consumer will return a response similar to:
```
{
  "claimSubmissionId: "1",
  "status": "IN_PROGRESS",
  "pdfData": ""
}
```

### Fetch PDF Process

When the consumer recieves a message in the `fetch-pdf` queue, it will use the provided `claimSubmissionId` to look it up on Redis.

If the PDF still hasn't been generated, you will recieve a response similar to:
```
{
  "claimSubmissionId: "1",
  "status": "IN_PROGRESS",
  "pdfData": ""
}
```

but if the PDF is available then the reponse will be:

```
{
  "claimSubmissionId: "1",
  "status": "COMPLETE",
  "pdfData": "..." //will contain string of base64 encoded pdf
}
```

You can either use the `status` field to check if the PDF is available or if `pdfData` is not empty

## Adding a new Diagnostic Code

1. Edit the `codes` dictionary in `config/settings.py` by adding a new key, value pair for your code.
     - Example:
    ```
    codes = {
      "0000": "cancer",
      "0001": "diabetes" //new code with human readable diagnosis
    }
    ```
2. Create a HTML version of the PDF you want to generate and save it in the `templates` folder
   - Take a look at [Jinja 2 Template Documentation](https://jinja.palletsprojects.com/en/3.1.x/templates/) for a better idea of what you can do within the template files
   - The file name should match the name you used in Step 1. Following that example, it should be called `diabetes.html`
3. Create a JSON file in `template_variables` that will contain default values for the HTML file in case they are not provided in the RabbitMQ message
    - The file name should match the name you used in Step 1 and 2. Following that example, it should be called `diabetes.json`
