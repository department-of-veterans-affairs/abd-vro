package gov.va.vro.mockbipce.api;

import org.openapitools.model.Payload;
import org.openapitools.model.UploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-01-22T14:21:59.944759-05:00[America/New_York]")
@Controller
public class FilesApiController implements FilesApi {
    @Override
    public ResponseEntity<UploadResponse> upload(String xFolderURI, Payload payload, MultipartFile file) {
        UploadResponse ur = new UploadResponse();
        ur.setOwner("VETERAN:FILENUMBER:987267855");
        return new ResponseEntity<>(ur, HttpStatus.OK);
    }
}
