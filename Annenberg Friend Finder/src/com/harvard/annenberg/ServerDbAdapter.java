package com.harvard.annenberg;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;

import android.util.Log;

public class ServerDbAdapter {

	public static String connectToServer(String serverUrl,
			Hashtable<String, String> params) {

		try {

			// Log.v("serverUrl", serverUrl);
			URL url = new URL(serverUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
			con.setRequestProperty("Connection", "Keep-Alive");
			String postString = "";

			Enumeration<String> keys = params.keys();
			String key, val;
			while (keys.hasMoreElements()) {
				key = keys.nextElement().toString();
				// Log.v("KEY", key);
				val = params.get(key);
				// Log.v("VAL", val);
				postString += key + "=" + URLEncoder.encode(val, "UTF-8") + "&";
			}
			postString = postString.substring(0, postString.length() - 1);
			Log.v("postString", postString);
			con.setRequestProperty("Content-Length",
					"" + Integer.toString(postString.getBytes().length));
			con.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			DataOutputStream dos = new DataOutputStream(con.getOutputStream());
			dos.writeBytes(postString);
			dos.flush();
			dos.close();
			// Responses from the server (code and message)
			int serverResponseCode = con.getResponseCode();
			Log.v("CODE", String.valueOf(serverResponseCode));
			String serverResponseMessage = con.getResponseMessage();
			Log.v("PAGE", serverResponseMessage);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			String json = response.toString();
			return json;

		} catch (MalformedURLException me) {
			Log.v("MalformedURLException", me.toString());
			return null;
		} catch (IOException ie) {
			Log.v("IOException", ie.toString());
			return null;

		} catch (Exception e) {
			Log.v("Exception", e.toString());
			return null;
			// Log.e("HREQ", "Exception: "+e.toString());
		}

	}

}
