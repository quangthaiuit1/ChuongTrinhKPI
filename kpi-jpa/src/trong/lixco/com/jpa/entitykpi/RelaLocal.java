package trong.lixco.com.jpa.entitykpi;

import javax.persistence.Entity;

/**
 * @author vantrong
 * @Time 16-04-2018 12:08:21 Quan he noi bo trong phong
 */
@Entity
public class RelaLocal extends AbstractEntity {
	private String handler;// Bao cao cho ai
	private String manager;// quan ly ai

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public static void main(String[] args) {
		int TH = 0, KH = 0, A = 0, B = 0;
		int res = ((TH - KH) * A) / B;

	}
}
