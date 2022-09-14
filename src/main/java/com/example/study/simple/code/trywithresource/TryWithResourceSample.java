package com.example.study.simple.code.trywithresource;

import java.io.*;

public class TryWithResourceSample {
    private static final int BUFFER_SIZEW = 8 * 1024;

    static void copy(String src, String dst) throws IOException {
        try(InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[BUFFER_SIZEW];
            int n;
            while ((n = in.read(buf)) >= 0) {
                out.write(buf, 0, n);
            }
        }
    }

    public static void main(String[] args) throws IOException{
        String src = args[0];
        String dst = args[1];
        copy(src, dst);
    }
}
