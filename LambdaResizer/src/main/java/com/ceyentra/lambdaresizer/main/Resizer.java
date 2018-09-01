package com.ceyentra.lambdaresizer.main;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.ceyentra.lambdaresizer.dto.ResizerInputDTO;
import org.apache.commons.io.FileUtils;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.UUID;

public class Resizer implements RequestHandler<ResizerInputDTO, String> {

    AmazonS3 s3client;

    @Override
    public String handleRequest(ResizerInputDTO dto, Context context) {
        String resizeUrl = createUrl(dto, context);
        if(!alreadyExists(resizeUrl)){
            BufferedImage originalImage =readImage(dto, context);
            if(originalImage != null){
                InputStream resizeImage = resizeImage(originalImage , dto, context);
                if(resizeImage != null){
                    if(!storeImage(resizeImage , resizeUrl , context)){
                        return "Failed to store image in S3";
                    }else{
                        return resizeUrl;
                    }
                }
                else{
                    return "Failed to resize Image";
                }
            }
            else{
                return "Failed to read Original image";
            }
        }

        return resizeUrl;
    }

    public String createUrl(ResizerInputDTO dto, Context context) {

        String resizeUrl = "";
        String publicUrl = System.getenv("publicurl");
        String fullHash = "" + Math.abs(dto.getUrl().hashCode());
        String fileName = "";

        try {
            fileName = Paths.get(new URI("").getPath()).getFileName().toString();


        } catch (URISyntaxException e) {
            e.printStackTrace();
            context.getLogger().log("unable to create url : " + dto.getUrl() + " , " + e.getMessage());
        }
        resizeUrl = publicUrl + fileName + " - " + fullHash + " - " + dto.getWidth() + " - " + dto.getHeight();

        return resizeUrl;
    }

    private BufferedImage readImage(ResizerInputDTO dto , Context context) {
        try {
            return ImageIO.read(new URL(dto.getUrl()).openStream());
        } catch (IOException e) {
            e.printStackTrace();
            context.getLogger().log("Failed to read original url : " + dto.getUrl() + " , " + e.getMessage());
            return null;
        }
    }

    private InputStream resizeImage(BufferedImage image , ResizerInputDTO dto, Context context){
        try{
            BufferedImage img=Scalr.resize(image, Scalr.Method.BALANCED , Scalr.Mode.AUTOMATIC , dto.getWidth());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(img , "gif" , os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            return is;
        }
        catch (IOException e){
            e.printStackTrace();
            context.getLogger().log("Image resizing failed... : " + dto.getUrl() + " , " + e.getMessage());
            return null;
        }
    }

    private AmazonS3 getS3Client(){
        if(s3client == null){
            s3client = new AmazonS3Client();
        }
        return s3client;
    }

    private String getS3Key(String resizeUrl){
        try {
            return Paths.get(new URI(resizeUrl).getPath()).getFileName().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "";
        }
    }

    private Boolean storeImage(InputStream is , String resizeUrl , Context context){
        String s3Key = getS3Key(resizeUrl);
        String bucketName = System.getenv("bucketname");
        File tempFile =null;

        try{
            tempFile = File.createTempFile(UUID.randomUUID().toString(), "gif");
            FileUtils.copyInputStreamToFile(is, tempFile);
            PutObjectRequest por = new PutObjectRequest(bucketName , s3Key ,tempFile).withCannedAcl(CannedAccessControlList.PublicRead);
            PutObjectResult res = getS3Client().putObject(por);
            context.getLogger().log("Stored in s3 : "+bucketName+" / "+ s3Key);

        }
        catch (IOException e){
            e.printStackTrace();
            context.getLogger().log("Error creating temp file : "+e.getMessage());
            return false;
        }
        finally {
            if(tempFile != null){
                tempFile.delete();
            }
        }
        return true;
    }

    private Boolean alreadyExists(String resizeUrl){
        String bucketName = System.getenv("bucketname");
        try{
            getS3Client().getObjectMetadata(bucketName, getS3Key(resizeUrl));
        }
        catch (AmazonServiceException e){
            return false;
        }
        return true;
    }
}
