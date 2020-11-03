package com.trunkfit.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class AmazonClient {

private static final String ACCESS_KEY ="AKIAU5REPHMAXCMPODXI";

private static final String PRIVATE_KEY ="E3Lj42vXLWl5io8fmfbUyQc/9yrO7ohhrCLpY43I";

private static final String BUCKET_NAME ="ilya-ilya-bucket";

private static final String S3_URL ="https://s3.us-east-2.amazonaws.com";

private AmazonS3 s3client;


@PostConstruct
    private void initializeAmazon() {
       this.s3client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2).withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(ACCESS_KEY,PRIVATE_KEY))).build();
}

public String uploadFile(MultipartFile multipartFile) {

  String fileUrl = "";
  try {
      File file = convertMultiPartToFile(multipartFile);
      String fileName = generateFileName(multipartFile);
      fileUrl = S3_URL + "/" + BUCKET_NAME + "/" + fileName;
      uploadFileTos3bucket(fileName, file);
      file.delete();
  } catch (Exception e) {
     e.printStackTrace();
  }
  return fileUrl;
}
private String generateFileName(MultipartFile multiPart) {
  return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
}
private File convertMultiPartToFile(MultipartFile file) throws IOException {
  File convFile = new File(file.getOriginalFilename());
  FileOutputStream fos = new FileOutputStream(convFile);
  fos.write(file.getBytes());
  fos.close();
  return convFile;
}
private void uploadFileTos3bucket(String fileName, File file) {
  s3client.putObject(new PutObjectRequest(BUCKET_NAME, fileName, file)
          .withCannedAcl(CannedAccessControlList.PublicRead));
}
}