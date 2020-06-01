package trong.lixco.com.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean
@SessionScoped
public class MediaKPIDepBean{
	private byte[] data;

	public void generatePrintOutput() throws IOException {
	}

	public StreamedContent getMedia() throws IOException {
		if (this.data == null) {
			return null;
		}
		InputStream stream = new ByteArrayInputStream(data);
		StreamedContent pdf = new DefaultStreamedContent(stream, "application/pdf");
		
		return pdf;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}


}