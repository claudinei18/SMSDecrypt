package com.tcc.smsdecrypt.criptografia;

/**
 * Created by claudinei on 03/09/17.
 */

import android.content.Context;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;

import com.tcc.smsdecrypt.LoginActivity;
import com.tcc.smsdecrypt.database.ChavesDbHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;


public class EncriptaDecriptaRSA extends AppCompatActivity {

    public final String ALGORITHM = "RSA";

    /**
     * Local da chave privada no sistema de arquivos.
     */
    public final String PATH_CHAVE_PRIVADA = Environment.getExternalStorageDirectory().getPath() + "/keys/" + LoginActivity.celularUsuarioLogado + "/private.key";

    /**
     * Local da chave pública no sistema de arquivos.
     */
    public final String PATH_CHAVE_PUBLICA = Environment.getExternalStorageDirectory().getPath() + "/keys/" + LoginActivity.celularUsuarioLogado + "/public.key";

    public void gerarChaveTeste(){
        int keySize = 512;
        SecureRandom random = new SecureRandom();
        // Choose two distinct prime numbers p and q.
        String a = "61";
        BigInteger p = new BigInteger(a);
        String b = "53";
        BigInteger q = new BigInteger(b);
        // Compute n = pq (modulus)
        BigInteger modulus = p.multiply(q);
        // Compute φ(n) = φ(p)φ(q) = (p − 1)(q − 1) = n - (p + q -1), where φ is Euler's totient function.
        // and choose an integer e such that 1 < e < φ(n) and gcd(e, φ(n)) = 1; i.e., e and φ(n) are coprime.
        BigInteger m = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        BigInteger publicExponent = getCoprime(m,random);
        // Determine d as d ≡ e−1 (mod φ(n)); i.e., d is the multiplicative inverse of e (modulo φ(n)).
        BigInteger privateExponent = publicExponent.modInverse(m);

        try {

            System.out.println(modulus);
            System.out.println(privateExponent);
            RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, publicExponent);
            RSAPrivateKeySpec privateSpec = new RSAPrivateKeySpec(modulus, privateExponent);

            KeyFactory factory = KeyFactory.getInstance("RSA");

            PublicKey pub = factory.generatePublic(spec);
            PrivateKey priv = factory.generatePrivate(privateSpec);

            System.out.println("Public Key : "+ byteArrayToHexString( pub.getEncoded() ));
            System.out.println("Private Key : "+ byteArrayToHexString( priv.getEncoded() ));
        }
        catch( Exception e ) {
            System.out.println(e.toString());
        }
    }

    public static BigInteger getCoprime(BigInteger m, SecureRandom random) {
        int length = m.bitLength()-1;
        BigInteger e = BigInteger.probablePrime(length,random);
        while (! (m.gcd(e)).equals(BigInteger.ONE) ) {
            e = BigInteger.probablePrime(length,random);
        }
        return e;
    }

    public static String byteArrayToHexString(byte[] bytes)
    {
        StringBuffer buffer = new StringBuffer();
        for(int i=0; i<bytes.length; i++)
        {
            if(((int)bytes[i] & 0xff) < 0x10)
                buffer.append("0");
            buffer.append(Long.toString((int) bytes[i] & 0xff, 16));
        }
        return buffer.toString();
    }

    /**
     * Gera a chave que conOtém um par de chave Privada e Pública usando 1025 bytes.
     * Armazena o conjunto de chaves nos arquivos private.key e public.key
     */
    public void geraChave(Context context) {
        try {
            System.out.println("Gerando chave");
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            RSAKeyGenParameterSpec kpgSpec = new RSAKeyGenParameterSpec(2048, BigInteger.valueOf(982451653));
            keyGen.initialize(kpgSpec);
            long a = System.currentTimeMillis();
            final KeyPair key = keyGen.generateKeyPair();
            long b = System.currentTimeMillis();
            System.out.println(b - a);
            System.out.println("Acima está tempo de geração de chave");
            PublicKey chavePublica = key.getPublic();

            System.out.println(2);

            RSAPrivateCrtKey crtKey = (RSAPrivateCrtKey) key.getPrivate();
            System.out.println("P : " + crtKey.getPrimeExponentP());
            System.out.println("Q : " + crtKey.getPrimeExponentQ());


            File chavePrivadaFile = new File(PATH_CHAVE_PRIVADA);
            File chavePublicaFile = new File(PATH_CHAVE_PUBLICA);

            // Cria os arquivos para armazenar a chave Privada e a chave Publica
            if (chavePrivadaFile.getParentFile() != null) {
                chavePrivadaFile.getParentFile().mkdirs();
            }

            System.out.println(chavePrivadaFile.createNewFile());

            if (chavePublicaFile.getParentFile() != null) {
                chavePublicaFile.getParentFile().mkdirs();
            }

            System.out.println(chavePublicaFile.createNewFile());

            // Salva a Chave Pública no arquivo
            ObjectOutputStream chavePublicaOS = new ObjectOutputStream(
                    new FileOutputStream(chavePublicaFile));
            chavePublicaOS.writeObject(key.getPublic());
            chavePublicaOS.close();

            // Salva a Chave Privada no arquivo
            ObjectOutputStream chavePrivadaOS = new ObjectOutputStream(
                    new FileOutputStream(chavePrivadaFile));
            chavePrivadaOS.writeObject(key.getPrivate());
            chavePrivadaOS.close();

            enviarChavesParaServidor(chavePublica, context);

//            gerarChaveTeste();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String publicKeyToString(PublicKey publicKey){
        // You can turn your bytes into a string using encoder.
        String keyAsString = Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
        // ....
        ///
        keyAsString = keyAsString.replaceAll("\n", "");
        // When you need your bytes back you call the decoder on the string.
        byte[] keyBytes = Base64.decode(keyAsString, Base64.DEFAULT);

        return keyAsString;
    }

    public PublicKey stringToPublicKey(String pub){
        try{
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decode(pub, Base64.DEFAULT));
            return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;

    }

    private void enviarChavesParaServidor(PublicKey chavePublicaNaMemoria, Context context) {

        ObjectInputStream inputStream = null;
        try {
            inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PUBLICA));
            final PublicKey chavePublica = (PublicKey) inputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // You can turn your bytes into a string using encoder.
        String keyAsString = publicKeyToString(chavePublicaNaMemoria);
        System.out.println(keyAsString);
        PublicKey a = stringToPublicKey(keyAsString);

        System.out.println(chavePublicaNaMemoria.equals(a));

        try{
            HttpURLConnection c = null;

            c = (HttpURLConnection) new URL("http://186.248.79.46:9201/chaves").openConnection();
            ChavesDbHelper db = new ChavesDbHelper(context);
            String tokenAccess = db.getAccessToken(LoginActivity.emailUsuarioLogado);
            System.out.println("tokenaccess: " + tokenAccess);
            final String oAuth = "Bearer " + tokenAccess;

            c.setRequestProperty ("Authorization", oAuth);

            c.setRequestMethod("POST");
            c.setConnectTimeout(5000);
            c.setRequestProperty("Content-Type", "application/json");
            c.setDoOutput(true);
            c.setDoInput(true);

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }


            System.out.println("ENVIANDO");
            System.out.println(publicKeyToString(chavePublicaNaMemoria));
            System.out.println("FIM");
            String json = "{\n" +
                    "\t\"celular\": \"" + LoginActivity.celularUsuarioLogado +"\",\n" +
                    "\t\"chavePublica\": \"" + publicKeyToString(chavePublicaNaMemoria) + "\",\n" +
                    "\t\"pessoa\": {\n" +
                    "\t\t\"codigo\":" + LoginActivity.codigoPessoaUsuarioLogado + "\n" +
                    "\t}\n" +
                    "}";


            OutputStream os = c.getOutputStream();
            os.write(json.getBytes("UTF-8"));
            os.close();

            c.getInputStream();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    public void deletarChaves(Context context){
        if(verificaSeExisteChavesNoSO(context)){
            File chavePrivada = new File(PATH_CHAVE_PRIVADA);
            File chavePublica = new File(PATH_CHAVE_PUBLICA);

            chavePrivada.delete();
            chavePublica.delete();
        }
        geraChave(context);
    }

    /**
     * Verifica se o par de chaves Pública e Privada já foram geradas.
     */
    public boolean verificaSeExisteChavesNoSO(Context context) {
        System.out.println(PATH_CHAVE_PRIVADA);
        System.out.println(PATH_CHAVE_PUBLICA);

        File chavePrivada = new File(PATH_CHAVE_PRIVADA);
        File chavePublica = new File(PATH_CHAVE_PUBLICA);

        if (chavePrivada.exists() && chavePublica.exists()) {
            System.out.println("EXISTEM");
            return true;
        }
        System.out.println("NAO EXISTEM");
        return false;
    }

    /**
     * Criptografa o texto puro usando chave pública.
     */
    public byte[] criptografa(String texto, PublicKey chave) {
        byte[] cipherText = null;

        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            // Criptografa o texto puro usando a chave Púlica
            cipher.init(Cipher.ENCRYPT_MODE, chave);
//            System.out.println(texto);
            cipherText = cipher.doFinal(texto.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cipherText;
    }

    /**
     * Decriptografa o texto puro usando chave privada.
     */
    public String decriptografa(byte[] texto, PrivateKey chave) {
        byte[] dectyptedText = null;

        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            // Decriptografa o texto puro usando a chave Privada
            cipher.init(Cipher.DECRYPT_MODE, chave);
            dectyptedText = cipher.doFinal(texto);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new String(dectyptedText);
    }

    public void teste(Context context){
        // Verifica se já existe um par de chaves, caso contrário gera-se as chaves..
        if (!verificaSeExisteChavesNoSO(context)) {
            // Método responsável por gerar um par de chaves usando o algoritmo RSA e
            // armazena as chaves nos seus respectivos arquivos.
                geraChave(context);
        }
    }

    public String criptografaPub(String msgOriginal, PublicKey chavePublica) throws IOException, ClassNotFoundException {
        ObjectInputStream inputStream = null;

//        inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PUBLICA));
//        final PublicKey chavePublica = (PublicKey) inputStream.readObject();
        final byte[] textoCriptografado = criptografa(msgOriginal, chavePublica);

        String decoded = new String(textoCriptografado, "ISO-8859-1");
//        System.out.println("decoded:" + decoded);

        byte[] encoded = decoded.getBytes("ISO-8859-1");
//        System.out.println("encoded:" + java.util.Arrays.toString(encoded));

        String[] a = java.util.Arrays.toString(encoded).split(",");


        String doc=new String(textoCriptografado, "ISO-8859-1");
        byte[] bytes2=doc.getBytes("ISO-8859-1");

        String c = new String(textoCriptografado, StandardCharsets.UTF_8);
        return java.util.Arrays.toString(encoded);
    }

    /**
     * Decriptografa o texto puro usando chave privada.
     */
    public String decriptografaPub(String mensagem) throws IOException, ClassNotFoundException {

        String[] texto = mensagem.split(",");
        byte[] arrayParaDecript = new byte[texto.length];
        for(int k = 0; k < texto.length; k++){
            String aa = texto[k].replaceAll(" ", "");
            aa = aa.replaceAll("\\[", "");
            aa = aa.replaceAll("\\]", "");

            arrayParaDecript[k] = Byte.parseByte(aa);

        }

        ObjectInputStream inputStream = null;


        // Decriptografa a Mensagem usando a Chave Pirvada
        inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PRIVADA));
        final PrivateKey chavePrivada = (PrivateKey) inputStream.readObject();

        System.out.println(chavePrivada.toString());

        final String textoPuro = decriptografa(arrayParaDecript, chavePrivada);

        return textoPuro;
    }

    /**
     * Testa o Algoritmo
     */
    public static void main(String[] args) {

        /*try {

            // Verifica se já existe um par de chaves, caso contrário gera-se as chaves..
            if (!verificaSeExisteChavesNoSO()) {
                // Método responsável por gerar um par de chaves usando o algoritmo RSA e
                // armazena as chaves nos seus respectivos arquivos.
                System.out.println("NÃO EXISTE");
//                geraChave();
            }

            *//*final String msgOriginal = "Exemplo de mensagem";
            ObjectInputStream inputStream = null;

            // Criptografa a Mensagem usando a Chave Pública
            inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PUBLICA));
            final PublicKey chavePublica = (PublicKey) inputStream.readObject();
            final byte[] textoCriptografado = criptografa(msgOriginal, chavePublica);

            // Decriptografa a Mensagem usando a Chave Pirvada
            inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PRIVADA));
            final PrivateKey chavePrivada = (PrivateKey) inputStream.readObject();
            final String textoPuro = decriptografa(textoCriptografado, chavePrivada);

            // Imprime o texto original, o texto criptografado e
            // o texto descriptografado.
            System.out.println("Mensagem Original: " + msgOriginal);
            System.out.println("Mensagem Criptografada: " + textoCriptografado.toString());
            System.out.println("Mensagem Decriptografada: " + textoPuro);*//*

        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
