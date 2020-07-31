package trong.lixco.com.thai.bean;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.inject.Named;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jboss.logging.Logger;
import org.omnifaces.cdi.ViewScoped;

import trong.lixco.com.bean.AbstractBean;
import trong.lixco.com.jpa.thai.KPIDepPerformanceJPA;

@Named
@ViewScoped
public class Mail extends AbstractBean<KPIDepPerformanceJPA> {

	private static final long serialVersionUID = 1L;

	public static boolean processSendMailPersonalSignKPI(String mailSend, String passMailSend, List<String> mailDestinations) {
//			notify = new Notify(FacesContext.getCurrentInstance());

//		MemberServicePublic employeeServicePublic = new MemberServicePublicProxy();
//		employee = employeeServicePublic.findByCode(codeEmplyee);

		try {

			// config mail server
			Properties props = System.getProperties();
//				props.put("mail.smtp.host", "mail.lixco.com");
//				props.put("mail.smtp.port", "25");

			// test
			props.put("mail.smtp.host", "mail.lixco.com");
			props.put("mail.smtp.port", "25");
			// end test

			// props.put("mail.smtp.auth", "true");
			// props.put("mail.smtp.starttls.enable", "true");
			// props.put("mail.smtp.host", "smtp.gmail.com");
			// props.put("mail.smtp.port", "587");

			Authenticator pa = null;
			if (mailSend != null && passMailSend != null) {
				props.put("mail.smtp.auth", "true");
				pa = new Authenticator() {
					public PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(mailSend, passMailSend);
					}
				};
			}
			Session session = Session.getInstance(props, pa);
			// Message msg = new MimeMessage(session);
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(mailSend));

			msg.setSubject("[Mail tự động] Nhắc nhở cá nhân thực hiện đăng ký KPI",
					"UTF-8");
			String text = "";

			Multipart multipart = new MimeMultipart("alternative");
			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText(text, "utf-8");

			// mã hóa
//			String strCreateByID = codeEmplyee;
//			String strIdcv = idcv + "";
//
//			String strContentTask = content_task;
//			String strContentComplete = content_complete;
//			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//			String strCreateDate = format.format(create_date);
//
//			String strRequestTypeId = requestType_id + "";
//			String strRequestById = request_by_id + "";
//
//			String strDatabase = database;
//			String encodeStrCreateById = EncodeDecode.encodeString(strCreateByID);
//			String encodeStrIdcv = EncodeDecode.encodeString(strIdcv);
//
//			String encodeStrContentTast = EncodeDecode.encodeString(strContentTask);
//			String encodeStrContentComplete = EncodeDecode.encodeString(strContentComplete);
//			String encodeStrCreateDate = EncodeDecode.encodeString(strCreateDate);
//			String encodeStrRequestTypeId = EncodeDecode.encodeString(strRequestTypeId);
//			String encodeStrRequestById = EncodeDecode.encodeString(strRequestById);
//
//			String encodeStrDatabase = EncodeDecode.encodeString(strDatabase);

			String htmlText = "<p style='font-size: 20px;'>Kính gửi anh/chị Trưởng đơn vị.</p>"
					+ "<p style='font-size: 20px;'>Hệ thống đánh giá KPI xin thông báo: Hôm nay đã là hạn cuối đăng ký KPI cá nhân, tuy nhiên hiện tại Anh/chị chưa hoàn thành việc đăng ký theo quy định. Vì vậy, Anh/chị vui lòng truy cập vào phần mềm để hoàn thành việc đăng ký KPI. Rất mong Anh/chị chị sớm hoàn thành theo đúng quy định KPI.</p>"
					+ "<p style='font-size: 20px;'>Trân trọng cảm ơn!</p>";

			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(htmlText, "text/html; charset=utf-8");

			multipart.addBodyPart(textPart);
			multipart.addBodyPart(htmlPart);
			msg.setContent(multipart);

			msg.setHeader("X-Mailer", "LOTONtechEmail");
			msg.setSentDate(new Date());
			msg.saveChanges();

			for(int i = 0; i < mailDestinations.size(); i++) {
				msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailDestinations.get(i), false));
			}
			Transport.send(msg);

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean processSendMailResultEvaluationPersonal(String mailSend, String passMailSend,
			List<String> mailDestinations) {
//		notify = new Notify(FacesContext.getCurrentInstance());

//	MemberServicePublic employeeServicePublic = new MemberServicePublicProxy();
//	employee = employeeServicePublic.findByCode(codeEmplyee);

		try {

			// config mail server
			Properties props = System.getProperties();
//			props.put("mail.smtp.host", "mail.lixco.com");
//			props.put("mail.smtp.port", "25");

			// test
			props.put("mail.smtp.host", "mail.lixco.com");
			props.put("mail.smtp.port", "25");
			// end test

			Authenticator pa = null;
			if (mailSend != null && passMailSend != null) {
				props.put("mail.smtp.auth", "true");
				pa = new Authenticator() {
					public PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(mailSend, passMailSend);
					}
				};
			}
			Session session = Session.getInstance(props, pa);
			// Message msg = new MimeMessage(session);
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(mailSend));

//		msg.setSubject("[" + idcv + "]" + "[Mail tự động] Nhắc nhở các đơn vị và cá nhân thực hiện đăng ký KPI",
//				"UTF-8");
			String text = "";

			Multipart multipart = new MimeMultipart("alternative");
			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText(text, "utf-8");

			String htmlText = "<p style='font-size: 20px;'>Kính gửi anh/chị Trưởng đơn vị.</p>"
					+ "<p style='font-size: 20px;'>Hệ thống đánh giá KPI xin thông báo: Hôm nay đã là hạn cuối đánh giá kết quả KPI cá nhân, tuy nhiên hiện tại Anh/chị chưa hoàn thành việc cập nhật dữ liệu chứng minh cũng như tính điểm kết quả KPI theo quy định. Vì vậy, Anh/chị vui lòng truy cập vào phần mềm để hoàn thành việc đánh giá kết quả KPI. Rất mong Anh/chị chị sớm hoàn thành theo đúng quy định KPI.</p>"
					+ "<p style='font-size: 20px;'>Trân trọng cảm ơn!</p>";

			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(htmlText, "text/html; charset=utf-8");

			multipart.addBodyPart(textPart);
			multipart.addBodyPart(htmlPart);
			msg.setContent(multipart);

			msg.setHeader("X-Mailer", "LOTONtechEmail");
			msg.setSentDate(new Date());
			msg.saveChanges();
			for(int i = 0; i < mailDestinations.size(); i++) {
				msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailDestinations.get(i), false));
			}
			Transport.send(msg);

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean processSendMailDepartmentSignKPI(String mailSend, String passMailSend,
			List<String> mailDestinations) {
//		notify = new Notify(FacesContext.getCurrentInstance());

//	MemberServicePublic employeeServicePublic = new MemberServicePublicProxy();
//	employee = employeeServicePublic.findByCode(codeEmplyee);

		try {

			// config mail server
			Properties props = System.getProperties();
//			props.put("mail.smtp.host", "mail.lixco.com");
//			props.put("mail.smtp.port", "25");

			// test
			props.put("mail.smtp.host", "mail.lixco.com");
			props.put("mail.smtp.port", "25");
			// end test

			Authenticator pa = null;
			if (mailSend != null && passMailSend != null) {
				props.put("mail.smtp.auth", "true");
				pa = new Authenticator() {
					public PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(mailSend, passMailSend);
					}
				};
			}
			Session session = Session.getInstance(props, pa);
			// Message msg = new MimeMessage(session);
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(mailSend));

		msg.setSubject("[Mail tự động] THÔNG BÁO ĐĂNG KÝ KPI PHÒNG ",
				"UTF-8");
			String text = "";

			Multipart multipart = new MimeMultipart("alternative");
			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText(text, "utf-8");

			String htmlText = "<p style='font-size: 20px;'>Kính gửi anh/chị Trưởng đơn vị,</p>"
					+ "<p style='font-size: 20px;'>Hệ thống đánh giá KPI xin thông báo: Hôm nay đã là hạn cuối đăng ký KPI phòng, tuy nhiên hiện tại Anh/chị chưa hoàn thành việc đăng ký theo quy định. Vì vậy, Anh/chị vui lòng truy cập vào phần mềm để hoàn thành việc đăng ký KPI. Rất mong Anh/chị chị sớm hoàn thành theo đúng quy định KPI.</p>"
					+ "<p style='font-size: 20px;'>Trân trọng cảm ơn!</p>";

			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(htmlText, "text/html; charset=utf-8");

			multipart.addBodyPart(textPart);
			multipart.addBodyPart(htmlPart);
			msg.setContent(multipart);

			msg.setHeader("X-Mailer", "LOTONtechEmail");
			msg.setSentDate(new Date());
			msg.saveChanges();
			
			for(int i = 0; i < mailDestinations.size(); i++) {
				msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailDestinations.get(i), false));
				Transport.send(msg);
			}
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean processSendMailResultEvaluationDepartment(String mailSend, String passMailSend,
			String mailDestination) {
//		notify = new Notify(FacesContext.getCurrentInstance());

//	MemberServicePublic employeeServicePublic = new MemberServicePublicProxy();
//	employee = employeeServicePublic.findByCode(codeEmplyee);

		try {

			// config mail server
			Properties props = System.getProperties();
//			props.put("mail.smtp.host", "mail.lixco.com");
//			props.put("mail.smtp.port", "25");

			// test
			props.put("mail.smtp.host", "mail.lixco.com");
			props.put("mail.smtp.port", "25");
			// end test

			Authenticator pa = null;
			if (mailSend != null && passMailSend != null) {
				props.put("mail.smtp.auth", "true");
				pa = new Authenticator() {
					public PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(mailSend, passMailSend);
					}
				};
			}
			Session session = Session.getInstance(props, pa);
			// Message msg = new MimeMessage(session);
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(mailSend));

//		msg.setSubject("[" + idcv + "]" + "[Mail tự động] Nhắc nhở các đơn vị và cá nhân thực hiện đăng ký KPI",
//				"UTF-8");
			String text = "";

			Multipart multipart = new MimeMultipart("alternative");
			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText(text, "utf-8");

			String htmlText = "<p style='font-size: 20px;'>Kính gửi anh/chị Trưởng đơn vị.</p>"
					+ "<p style='font-size: 20px;'>Hệ thống đánh giá KPI xin thông báo: Hôm nay đã là hạn cuối đánh giá kết quả KPI phòng, tuy nhiên hiện tại Anh/chị chưa hoàn thành việc cập nhật dữ liệu chứng minh cũng như tính điểm kết quả KPI theo quy định. Vì vậy, Anh/chị vui lòng truy cập vào phần mềm để hoàn thành việc đánh giá kết quả KPI. Rất mong Anh/chị chị sớm hoàn thành theo đúng quy định KPI.</p>"
					+ "<p style='font-size: 20px;'>Trân trọng cảm ơn!</p>";

			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(htmlText, "text/html; charset=utf-8");

			multipart.addBodyPart(textPart);
			multipart.addBodyPart(htmlPart);
			msg.setContent(multipart);

			msg.setHeader("X-Mailer", "LOTONtechEmail");
			msg.setSentDate(new Date());
			msg.saveChanges();

			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailDestination, false));
			Transport.send(msg);

			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	protected void initItem() {
		// TODO Auto-generated method stub

	}

	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}
}
