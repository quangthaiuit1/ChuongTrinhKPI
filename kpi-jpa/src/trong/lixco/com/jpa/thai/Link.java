package trong.lixco.com.jpa.thai;

import javax.persistence.Entity;
import javax.persistence.Table;

import trong.lixco.com.jpa.entitykpi.AbstractEntity;

@Entity
@Table(name = "link")
public class Link extends AbstractEntity {
	private String link_name;
	private String link_jdbc;
	private String username;
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLink_name() {
		return link_name;
	}

	public void setLink_name(String link_name) {
		this.link_name = link_name;
	}

	public String getLink_jdbc() {
		return link_jdbc;
	}

	public void setLink_jdbc(String link_jdbc) {
		this.link_jdbc = link_jdbc;
	}
}