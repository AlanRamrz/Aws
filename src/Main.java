import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;

public class Main {

	private static final String clientRegion = Region.getRegion(Regions.US_EAST_2).toString();
	private static final String access = "";
	private static final String secret = "";

	public static void main(String[] args) {
		//createBucket("esteesunbuckettestjava17");
		//uploadFile("/Users/alanramirez/Downloads/hola2.txt");
		//uploadFileFromStream();
		getFilesFromBucket();
	}

	private static void createBucket(String bucketName) {
		try {
			BasicAWSCredentials credentials = new BasicAWSCredentials(access, secret);
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(clientRegion).build();
			//AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new ProfileCredentialsProvider()).withRegion(clientRegion).build();

			if (!s3Client.doesBucketExistV2(bucketName)) {
				// Because the CreateBucketRequest object doesn't specify a region, the
				// bucket is created in the region specified in the client.
				s3Client.createBucket(new CreateBucketRequest(bucketName));

				// Verify that the bucket was created by retrieving it and checking its
				// location.
				String bucketLocation = s3Client.getBucketLocation(new GetBucketLocationRequest(bucketName));
				System.out.println("Bucket location: " + bucketLocation);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	private static void uploadFile(String rutaArchivo) {
		try {
			BasicAWSCredentials credentials = new BasicAWSCredentials(access, secret);
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(clientRegion).build();
			//AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new ProfileCredentialsProvider()).withRegion(clientRegion).build();
			File file = new File(rutaArchivo);
			
			PutObjectRequest request = new PutObjectRequest("esteesunbuckettestjava", "archivo.txt", file);
			ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("plain/text");
            metadata.addUserMetadata("x-amz-meta-title", "someTitle");
            request.setMetadata(metadata);
			s3Client.putObject(request);
			
			s3Client.putObject("lambda-bucket-uia-images", "nombre.txt", "Este es el contenido del archivo");
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void uploadFileFromStream() {
		try {
			BasicAWSCredentials credentials = new BasicAWSCredentials(access, secret);
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(clientRegion).build();
			
			InputStream in = new URL("https://www.ecured.cu/images/thumb/c/c3/Francisco_Guillermo_Ochoa_Maga%C3%B1a1.jpg/260px-Francisco_Guillermo_Ochoa_Maga%C3%B1a1.jpg").openStream();
			byte[] bytes = IOUtils.toByteArray(in);
			
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
			
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(bytes.length);
			
			PutObjectRequest request = new PutObjectRequest("lambda-bucket-uia-images", "imagen.jpg", byteArrayInputStream, metadata).withCannedAcl(CannedAccessControlList.PublicRead);
			
			s3Client.putObject(request);
			
			in.close();
			byteArrayInputStream.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void getFilesFromBucket() {
		try {
			BasicAWSCredentials credentials = new BasicAWSCredentials(access, secret);
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(clientRegion).build();
			
			S3Object object = s3Client.getObject(new GetObjectRequest("lambda-bucket-uia-images", "imagen.jpg"));
			InputStream objectData = object.getObjectContent();
			
			System.out.println("EL URL ES: "+s3Client.getUrl("lambda-bucket-uia-images", "imagen.jpg").toExternalForm());
			
			File file = new File("/Users/alanramirez/Downloads/descargaAws.jpg");
			OutputStream out = new FileOutputStream(file);
			
			IOUtils.copy(objectData, out);
			
			objectData.close();
			out.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
