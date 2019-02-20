package commons.init;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import commons.model.Device;
import commons.utils.Console;
import commons.utils.Constants;
import commons.utils.FileUtil;
import commons.utils.SecurityUtil;

public class SecurityUtilX extends SecurityUtil {

	private static final String CIPHER_ALGORITHM = "PBEWithSHA1AndDESede";

	private static final String DIRECTORY_NAME = "keystores";

	private static final String SSL_KEYSTORE_SECRET = "hkRPusjglo";

	SecurityUtilX() {
		super();
	}
	
	
	
	//1.
	//Establishes connection to the IoT Core API - using:   Device and Authentication
/*
	public static SSLSocketFactory getSSLSocketFactory(Device device, Authentication authentication)
	throws GeneralSecurityException, IOException {
		String secret = authentication.getSecret();
		String pem = authentication.getPem();

		String pemCertificate = pem.substring(
			pem.indexOf("-----BEGIN CERTIFICATE-----\n") + "-----BEGIN CERTIFICATE-----\n".length(),
			pem.indexOf("\n-----END CERTIFICATE-----\n"));

		String pemPrivateKey = pem.substring(
			pem.indexOf("-----BEGIN ENCRYPTED PRIVATE KEY-----\n") +
				"-----BEGIN ENCRYPTED PRIVATE KEY-----\n".length(),
			pem.indexOf("\n-----END ENCRYPTED PRIVATE KEY-----\n"));

		//2.
		KeyManager[] keyManagers = getKeyManagers(device, pemCertificate, pemPrivateKey, secret);
		
		//3. 
		TrustManager[] trustManagers = getTrustManagers();

		//5.
		return getSSLSocketFactory(keyManagers, trustManagers);
	}
*/

	//5.
	//Establishes connection to the IoT Core API - using:   KeyManager[] and TrustManager[]
//MY VERSION - requires no inputs
	public static SSLSocketFactory getSSLSocketFactory( String suffix, String keystoreSecret )
	throws GeneralSecurityException, IOException {

		KeyManager[] keyManagers = getKeyManagers( suffix, keystoreSecret );
		TrustManager[] trustManagers = getTrustManagers();

		SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
		sslContext.init(keyManagers, trustManagers, new java.security.SecureRandom());

		return sslContext.getSocketFactory();
	}


	//2.
	private static KeyManager[] getKeyManagers(Device device, String pem, String encryptedPrivateKey, String secret)
	throws GeneralSecurityException, IOException {
		//4.
		PrivateKey privateKey = decryptPrivateKey(encryptedPrivateKey, secret);

		ByteArrayInputStream is = new ByteArrayInputStream(
			Base64.getMimeDecoder().decode(pem.getBytes(Constants.DEFAULT_ENCODING)));

		Certificate certificate;
		try {
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			certificate = certificateFactory.generateCertificate(is);
		}
		finally {
			FileUtil.closeStream(is);
		}

		/*
		Path destinationPath = null;
		try {
			File jar = new File(AbstractPropertiesHelper.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			destinationPath = jar.getParentFile().getAbsolutePath().concat(System.getProperty( "file.separator" )).concat( DIRECTORY_NAME );
			Console.printText( String.format( "Looking for keystores directory here: %1$s", destinationPath ) );

			destination = Files.createTempDirectory(TEMP_DIRECTION_NAME);
		}
		catch (IllegalArgumentException | SecurityException | IOException e) {
			throw new IOException("Unable to initialize a destination to store PEM", e);
		}

		File p12KeyStore = new File(destination.toFile(),
		*/
		String keystoresDir = AbstractPropertiesHelper.class.getProtectionDomain().getCodeSource().getLocation().getPath().concat( DIRECTORY_NAME );
		Console.printText( String.format( "Looking for keystores directory here: %1$s", keystoresDir ) );
		File p12KeyStore = new File( keystoresDir,
			device.getAlternateId().replaceAll(":", "") + ".p12");

		KeyStore keyStore;
		try {
			keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(null, secret.toCharArray());
			/*
			try (FileOutputStream p12KeyStoreStream = new FileOutputStream(p12KeyStore)) {
				keyStore.store(p12KeyStoreStream, SSL_KEYSTORE_SECRET.toCharArray());

				keyStore.setKeyEntry("private", privateKey, SSL_KEYSTORE_SECRET.toCharArray(),
					new Certificate[] { certificate });

				keyStore.store(p12KeyStoreStream, SSL_KEYSTORE_SECRET.toCharArray());
			}

		}
		catch (GeneralSecurityException | IOException e) {
			//TODO   DISABLED THE DELETE
			//FileUtil.deletePath(destination);

			throw new KeyManagementException("Unable to initialize P12 key store", e);
		}

		try {
		*/
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, SSL_KEYSTORE_SECRET.toCharArray());

			return keyManagerFactory.getKeyManagers();
		}
		finally {
			//TODO   DISABLED THE DELETE
			//FileUtil.deletePath(destination);
		}
	}

	
	//MY VERSION
	private static KeyManager[] getKeyManagers( String suffix, String keystoreSecret )    //Device device, String pem, String encryptedPrivateKey, String secret)
			throws GeneralSecurityException, IOException {

		// Call before>>>  String keystoreSecret = deviceProperties.getKeystoreSecret();
		KeyStore keyStore = SecurityUtilX.openPkcs12Keystore( suffix, keystoreSecret );
		
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init( keyStore, keystoreSecret.toCharArray());
	
		return keyManagerFactory.getKeyManagers();
	}


	
	
	//4.
	// Required, because it's marked as 'private' in the superclass
	private static PrivateKey decryptPrivateKey(String encryptedPrivateKey, String secret)
	throws GeneralSecurityException, IOException {

		byte[] encodedPrivateKey = Base64.getMimeDecoder()
			.decode(encryptedPrivateKey.getBytes(Constants.DEFAULT_ENCODING));

		EncryptedPrivateKeyInfo encryptPKInfo = new EncryptedPrivateKeyInfo(encodedPrivateKey);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		PBEKeySpec pbeKeySpec = new PBEKeySpec(secret.toCharArray());
		SecretKeyFactory secretFactory = SecretKeyFactory.getInstance(CIPHER_ALGORITHM);
		Key pbeKey = secretFactory.generateSecret(pbeKeySpec);
		AlgorithmParameters algorithmParameters = encryptPKInfo.getAlgParameters();
		cipher.init(Cipher.DECRYPT_MODE, pbeKey, algorithmParameters);
		KeySpec pkcsKeySpec = encryptPKInfo.getKeySpec(cipher);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		return keyFactory.generatePrivate(pkcsKeySpec);
	}


	/*
	 * Do not use in production! This trust manager trusts whatever certificate is provided.
	 * 
	 * When connecting through wss with a broker which uses a self-signed certificate or a
	 * certificate that is not trusted by default, there are two options.
	 * 
	 * 1. Disable host verification. This should only be used for testing. It is not recommended in
	 * productive environments.
	 * 
	 * options.setSocketFactory(getTrustManagers()); // will trust all certificates
	 * 
	 * 2. Add the certificate to your keystore. The default keystore is located in the JRE in <jre
	 * home>/lib/security/cacerts. The certificate can be added with
	 * 
	 * "keytool -import -alias my.broker.com -keystore cacerts -file my.broker.com.pem".
	 * 
	 * It is also possible to point to a custom keystore:
	 * 
	 * Properties properties = new Properties();
	 * properties.setProperty("com.ibm.ssl.trustStore","my.cacerts");
	 * options.setSSLProperties(properties);
	 */
	//3.
//copy - since superclass is private
	private static TrustManager[] getTrustManagers() {
		return new TrustManager[] { new X509TrustManager() {

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
			throws java.security.cert.CertificateException {
				// empty implementation
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
			throws java.security.cert.CertificateException {
				// empty implementation
			}

		} };
	}

	
	public static KeyStore openPkcs12Keystore( String suffix, String keystoreSecret )
			throws IOException, FileNotFoundException {
		KeyStore keystore = null;
		try {
			String deviceP12KeystoreFilename = String.format( "device%1$s.pkcs12", suffix );
            
			File jar = new File(AbstractPropertiesHelper.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    		String path = jar.getParentFile().getAbsolutePath().concat(System.getProperty( "file.separator" )).concat( deviceP12KeystoreFilename );
    		Console.printText( String.format( "Looking for Keystore file here: %1$s", path ) );

			File deviceP12KeystoreFile = new File( deviceP12KeystoreFilename );
			if ( deviceP12KeystoreFile.exists() ) {
				//Attempt to open Keystore
//TODO				ks = SecurityUtilX.openPkcs12Truststore( deviceP12KeystoreFilename, keystoreSecret );
				keystore = KeyStore.getInstance( "PKCS12" );
	            char[] keystoreSecret_char = keystoreSecret.toCharArray();

	            InputStream readStream = new FileInputStream( path );

	            keystore.load(
	            	readStream,  //Assumes on classpath:  this.getClass().getClassLoader().getResourceAsStream("the.p12"),
	            	keystoreSecret_char );

	            //Downloaded PKCS#12 files from IoT Core appear to store their Private Key with an alias of "1"
	            PrivateKey key = (PrivateKey) keystore.getKey("1", keystoreSecret_char );
	            if ( key != null ) {
	            	Console.printText( "Private Key successfully retrieved from keystore: " + deviceP12KeystoreFile);
	            }
			}

        } catch (Exception e) {
            Console.printError( "Exception while trying to obtain private key. \tFurther details: " + e );
            return null;
        }
		return keystore; 
	}

		
	/**
	 * Download and persist the Device's PKCS#12 Truststore to disk
	 * @param coreService
	 * @param device
	 * @param suffix
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static String downloadPkcs12Keystore(CoreServiceX coreService, Device device, String suffix )
			throws IOException, FileNotFoundException {
		//Basic cleanup
		String deviceP12KeystoreFilename = String.format( "device%1$s.pkcs12", suffix );
		File deviceP12KeystoreFile = new File( deviceP12KeystoreFilename );
        if ( deviceP12KeystoreFile.exists() ) {
        	deviceP12KeystoreFile.delete();
        }
        
        //Download PKCS#12 via API
		AuthenticationX authP12 = coreService.getAuthenticationX( device );
		
		String base64p12 = authP12.getP12();
		ByteArrayInputStream in = new ByteArrayInputStream(
				Base64.getMimeDecoder().decode( base64p12.getBytes(Constants.DEFAULT_ENCODING)));
		OutputStream out = new FileOutputStream( deviceP12KeystoreFile );

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
		    out.write(buf, 0, len);
		}
		in.close();
		out.close();
		
		String keystoreSecret = authP12.getSecret();
		return keystoreSecret;
	}

}