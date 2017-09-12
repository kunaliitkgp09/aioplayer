package com.aioplayer.server;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.aioplayer.util.NanoHTTPD;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by akankshadhanda on 16/07/17.
 */

public class FileServer extends NanoHTTPD {
   private String uriPath;
    private Context mContext;
    private Bitmap bitmap;


        public FileServer (Context context,String uriPath,int portNumber) {
            super(portNumber);

            this.uriPath=uriPath;
            this.mContext=context;

        }
    public FileServer (Context context, Bitmap bitmap, int portNumber) {
        super(portNumber);
        this.bitmap=bitmap;
        this.mContext=context;

    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> headers = session.getHeaders();
        Map<String, String> parms = session.getParms();
        Method method = session.getMethod();
        String uri = session.getUri();
        Map<String, String> files = new HashMap<>();

        if (Method.POST.equals(method) || Method.PUT.equals(method)) {
            try {
                session.parseBody(files);
            }
            catch (IOException e) {
                return getResponse("Internal Error IO Exception: " + e.getMessage());
            }
            catch (ResponseException e) {
                return new Response(e.getStatus(), MIME_PLAINTEXT, e.getMessage());
            }
        }

        uri = uri.trim().replace(File.separatorChar, '/');
        if (uri.indexOf('?') >= 0) {
            uri = uri.substring(0, uri.indexOf('?'));
        }

        File f = new File(uriPath);
        return serveFile(uri, headers, f);
    }

    private Response serveFile(String uri, Map<String, String> header, File file) {
        Response res;
        String mime = getType(file.getName());
        try {
            // Calculate etag
            String etag = Integer.toHexString((file.getAbsolutePath() +
                    file.lastModified() + "" + file.length()).hashCode());

            // Support (simple) skipping:
            long startFrom = 0;
            long endAt = -1;
            String range = header.get("range");
            if (range != null) {
                if (range.startsWith("bytes=")) {
                    range = range.substring("bytes=".length());
                    int minus = range.indexOf('-');
                    try {
                        if (minus > 0) {
                            startFrom = Long.parseLong(range.substring(0, minus));
                            endAt = Long.parseLong(range.substring(minus + 1));
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            // Change return code and add Content-Range header when skipping is requested
            long fileLen = file.length();
            if (range != null && startFrom >= 0) {
                if (startFrom >= fileLen) {
                    res = createResponse(Response.Status.RANGE_NOT_SATISFIABLE, MIME_PLAINTEXT, "");
                    res.addHeader("Content-Range", "bytes 0-0/" + fileLen);
                    res.addHeader("ETag", etag);
                } else {
                    if (endAt < 0) {
                        endAt = fileLen - 1;
                    }
                    long newLen = endAt - startFrom + 1;
                    if (newLen < 0) {
                        newLen = 0;
                    }

                    final long dataLen = newLen;
                    FileInputStream fis = new FileInputStream(file) {
                        @Override
                        public int available() throws IOException {
                            return (int) dataLen;
                        }
                    };
                    fis.skip(startFrom);

                    res = createResponse(Response.Status.PARTIAL_CONTENT, mime, fis);
                    res.addHeader("Content-Length", "" + dataLen);
                    res.addHeader("Content-Range", "bytes " + startFrom + "-" +
                            endAt + "/" + fileLen);
                    res.addHeader("ETag", etag);
                }
            } else {
                if (etag.equals(header.get("if-none-match")))
                    res = createResponse(Response.Status.NOT_MODIFIED, mime, "");
                else {
                    res = createResponse(Response.Status.OK, mime, new FileInputStream(file));
                    res.addHeader("Content-Length", "" + fileLen);
                    res.addHeader("ETag", etag);
                }
            }
        } catch (IOException ioe) {
            res = getResponse("Forbidden: Reading file failed");
        }

        return (res == null) ? getResponse("Error 404: File not found") : res;
    }

    // Announce that the file server accepts partial content requests
    private Response createResponse(Response.Status status, String mimeType, InputStream message) {
        Response res = new Response(status, mimeType, message);
        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }

    // Announce that the file server accepts partial content requests
    private Response createResponse(Response.Status status, String mimeType, String message) {
        Response res = new Response(status, mimeType, message);
        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }

    public String getType(String uriPath)
    {
        if(uriPath.endsWith(".m4a"))
        {
            return "audio/m4a";
        }
        else if(uriPath.endsWith(".mp4"))
        {
            return "videos/mp4";
        }
        else if(uriPath.endsWith(".mp3"))
        {
            return "audios/mp3";
        }
        else if(uriPath.endsWith(".vp8"))
        {
            return "videos/webm";
        }
        else if(uriPath.endsWith(".aac")) {
            return  "audio/x-aac";
        }
        else if(uriPath.endsWith(".wav")) {
            return "audio/x-wav";
        }
        else if(uriPath.endsWith(".gif")) {
            return "image/gif";
        }
        else if(uriPath.endsWith(".bmp")) {
            return  "image/bmp";
        }
        else if(uriPath.endsWith(".jpeg")||uriPath.endsWith(".jpg")) {
            return "image/jpeg";
        }
        else if(uriPath.endsWith(".png")) {
            return "image/png";
        }
        else if(uriPath.endsWith(".webp")) {
            return  "image/webp";
        }
        else
        {
            return "";
        }
    }

    private Response getResponse(String message) {
        return createResponse(Response.Status.OK, "text/plain", message);
    }
}
