package com.teclever.dfcc.utils;

import java.io.File;

import java.security.MessageDigest;

import java.io.FileInputStream;

import java.io.InputStream;





//ChecksumUtils.ChecksumDetails calculatedChecksumDetails = ChecksumUtils.getMD5Checksum(filePath); //--pass filepath

//String calculatedChecksumValue = calculatedChecksumDetails.getChecksumValue(); //--get checksumvalue

//long calculatedFileSize = calculatedChecksumDetails.getFileSize(); //--get filesize

public class ChecksumUtils {



    public static class ChecksumDetails {

        private final String checksumValue;

        private final long fileSize;



        public ChecksumDetails(String checksumValue, long fileSize) {

            this.checksumValue = checksumValue;

            this.fileSize = fileSize;

        }



        public String getChecksumValue() {

            return checksumValue;

        }



        public long getFileSize() {

            return fileSize;

        }

    }



    public static ChecksumDetails getMD5Checksum(String filename) throws Exception {

        File file = new File(filename);

        long fileSize = file.length();



        byte[] b = createChecksum(filename);

        StringBuilder result = new StringBuilder();



        for (int i = 0; i < b.length; i++) {

            result.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));

        }



        return new ChecksumDetails(result.toString(), fileSize);

    }



    private static byte[] createChecksum(String filename) throws Exception {

        InputStream fis = new FileInputStream(filename);

        byte[] buffer = new byte[1024];

        MessageDigest complete = MessageDigest.getInstance("MD5");

        int numRead;



        do {

            numRead = fis.read(buffer);

            if (numRead > 0) {

                complete.update(buffer, 0, numRead);

            }

        } while (numRead != -1);



        fis.close();

        return complete.digest();

    }





}


