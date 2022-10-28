package com.nls.Enquiry;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

public class XMLEntity extends StringEntity {

	private String xmlContent;

	public XMLEntity(String xmlContent) throws UnsupportedEncodingException {
		super(xmlContent);
		this.xmlContent = xmlContent;
		Header h = new BasicHeader(HTTP.CONTENT_TYPE, "text/xml;charset=UTF-8");
		setContentType(h);
	}

	@Override
	public InputStream getContent() throws IOException, UnsupportedOperationException {
		// TODO Auto-generated method stub
		return new ByteArrayInputStream(xmlContent.getBytes());
	}

	@Override
	public long getContentLength() {
		return xmlContent.length();
	}

	@Override
	public boolean isRepeatable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isStreaming() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void writeTo(OutputStream os) throws IOException {
		os.write(xmlContent.getBytes());
		os.flush();

	}

	@Override
	public Header getContentType() {

		return new BasicHeader(HTTP.CONTENT_TYPE, "text/xml;charset=UTF-8");
	}

	@Override
	public void consumeContent() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Header getContentEncoding() {
		return null;
	}

	@Override
	public boolean isChunked() {
		// TODO Auto-generated method stub
		return false;
	}

}
