//package trong.lixco.com.thai.bean;
//
//import java.util.Date;
//import java.util.Properties;
//
//import javax.faces.context.FacesContext;
//import javax.inject.Named;
//import javax.mail.Authenticator;
//import javax.mail.Message;
//import javax.mail.Multipart;
//import javax.mail.PasswordAuthentication;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeBodyPart;
//import javax.mail.internet.MimeMessage;
//import javax.mail.internet.MimeMultipart;
//
//import org.omnifaces.cdi.ViewScoped;
//
//import com.ibm.icu.text.SimpleDateFormat;
//
//import trong.lixco.com.account.servicepublics.MemberServicePublic;
//import trong.lixco.com.account.servicepublics.MemberServicePublicProxy;
//import trong.lixco.com.util.Notify;
//
//@Named
//@ViewScoped
//public class Mail {
//	public static boolean sendMailTPRepairRequestNotCD(String mailAdmin, String passMailAdmin, String toEmail,
//			String codeEmplyee, Long idcv, String content_task, String content_complete, Date create_date,
//			Long requestType_id, Long request_by_id, String server, String device_name, String noiSuaChua,
//			String nameDepartPlace, String reason, String database, String serverPublic) {
////			notify = new Notify(FacesContext.getCurrentInstance());
//
//		try {
//			MemberServicePublic employeeServicePublic = new MemberServicePublicProxy();
//			employee = employeeServicePublic.findByCode(codeEmplyee);
//
//			if (codeEmplyee != null && codeEmplyee != "") {
//
//				Properties props = System.getProperties();
//				props.put("mail.smtp.host", "mail.lixco.com");
//				props.put("mail.smtp.port", "25");
//
//				// props.put("mail.smtp.auth", "true");
//				// props.put("mail.smtp.starttls.enable", "true");
//				// props.put("mail.smtp.host", "smtp.gmail.com");
//				// props.put("mail.smtp.port", "587");
//
//				Authenticator pa = null;
//				if (mailAdmin != null && passMailAdmin != null) {
//					props.put("mail.smtp.auth", "true");
//					pa = new Authenticator() {
//						public PasswordAuthentication getPasswordAuthentication() {
//							return new PasswordAuthentication(mailAdmin, passMailAdmin);
//						}
//					};
//				}
//				Session session = Session.getInstance(props, pa);
//				// Message msg = new MimeMessage(session);
//				MimeMessage msg = new MimeMessage(session);
//				msg.setFrom(new InternetAddress(mailAdmin));
//
//				msg.setSubject("[" + idcv + "]" + "[Mail tự động] Nhắc nhở các đơn vị và cá nhân thực hiện đăng ký KPI", "UTF-8");
//				String text = "";
//
//				Multipart multipart = new MimeMultipart("alternative");
//				MimeBodyPart textPart = new MimeBodyPart();
//				textPart.setText(text, "utf-8");
//
//				// mã hóa
//				String strCreateByID = codeEmplyee;
//				String strIdcv = idcv + "";
//
//				String strContentTask = content_task;
//				String strContentComplete = content_complete;
//				SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//				String strCreateDate = format.format(create_date);
//
//				String strRequestTypeId = requestType_id + "";
//				String strRequestById = request_by_id + "";
//
//				String strDatabase = database;
//
//				String encodeStrCreateById = encodeDecode.encodeString(strCreateByID);
//				String encodeStrIdcv = encodeDecode.encodeString(strIdcv);
//
//				String encodeStrContentTast = encodeDecode.encodeString(strContentTask);
//				String encodeStrContentComplete = encodeDecode.encodeString(strContentComplete);
//				String encodeStrCreateDate = encodeDecode.encodeString(strCreateDate);
//				String encodeStrRequestTypeId = encodeDecode.encodeString(strRequestTypeId);
//				String encodeStrRequestById = encodeDecode.encodeString(strRequestById);
//
//				String encodeStrDatabase = encodeDecode.encodeString(strDatabase);
//
//				// String htmlText = "<p>Kính gửi Trưởng đơn vị.</p>"
//// + "<p>Đây là thông báo xác nhận yêu cầu đi công tác của nhân
//				// viên, được gửi tự động từ hệ thống.</p>" + "<p>"
//				// + "Yêu cầu của:" + "<p>" + " <b
//				// style='background:antiquewhite'>" +
//				// employee.getEmployeeName()
//				// + ".<b></b>"
//				// + "<p>" + "</b>Lý do:" + "<p>" + " <b>" + content_complete +
//				// ".</b>"
//				// + "<p>" + "</b>Nội dung công tác:" + "<p>" + " <b>" +
//				// content_task + ".</b>"
//				// + "<p>Vui lòng nhấn vào <a href=\"" + server +
//				// "/REQUEST_WEB/activated/confirmworktp.jsf?idmem="
//				// + encodeStrCreateById + "&idcv=" + encodeStrIdcv +
//				// "&contenttask=" + encodeStrContentTast
//				// + "&contentcomplete=" + encodeStrContentComplete +
//				// "&createdate=" + encodeStrCreateDate
//				// + "&requesttypeid=" + encodeStrRequestTypeId +
//				// "&requestbyid=" + encodeStrRequestById
//				// + "\">đây</a> để xác nhận yêu cầu.</p>" + "<p>Xin chân thành
//				// cảm ơn.</p>";
//
////				String htmlText = "<p>Hệ thống đặt lịch công tác thông báo xác nhận yêu cầu công tác của nhân viên.</p>"
////						+ "Nhân viên yêu cầu:" + "<p>" + " <b style='background:antiquewhite'>"
////						+ employee.getEmployeeName() + ".<b></b>" + "<p>" + "</b>Nội dung công tác:" + "<p>" + " <b>"
////						+ content_task + ".</b>" + "<p>Nhấn vào <a href=\"" + server
////						+ "/REQUEST_WEB/activated/confirmworktp.jsf?idmem=" + encodeStrCreateById + "&idcv="
////						+ encodeStrIdcv + "&contenttask=" + encodeStrContentTast + "&contentcomplete="
////						+ encodeStrContentComplete + "&createdate=" + encodeStrCreateDate + "&requesttypeid="
////						+ encodeStrRequestTypeId + "&requestbyid=" + encodeStrRequestById
////						+ "\">đây</a> để xác nhận yêu cầu.</p>";
//
//				String htmlText = "<p style='font-size: 20px;'>Kính gửi trưởng đơn vị.</p>"
//						+ "<p style='font-size: 20px;'>Đây là yêu cầu sửa chữa của nhân viên.</p>"
//						+ "<div style='max-width: 100%; overflow-x: scroll;'>"
//						+ "<table cellspacing='0' cellpadding='0' style='width:100%; border:3px solid #ccc; border-bottom:1px solid #eee; font-size:15px; line-height:135%'>"
//						+ "<tr style='background-color:#f5f5f5'>"
//						+ "<th style='vertical-align:top ;color:#222; text-align:left; padding:7px 9px 7px 9px; border-top:1px solid #eee'>Họ tên nhân viên: <span style='color:red'>*</span></th>"
//						+ "<td style='vertical-align:top;color:#333;width:60%;padding:7px 9px 7px 0;border-top:1px solid #eee'>"
//						+ employee.getName() + "</td>"
////	             + "/tr>"
//						+ "<tr style=''>"
//						+ "  <th style='vertical-align: top;color:#222; text-align:left; padding:7px 9px 7px 9px; border-top:1px solid #eee'>Ngày tạo yêu cầu: <span style='color:red'>*</span></th>"
//						+ "          <td style='vertical-align:top;color:#333;width:60%;padding:7px 9px 7px 0;border-top:1px solid #eee'>"
//						+ strCreateDate + "</td>" + "</tr>" + "<tr style=''>"
//						+ "  <th style='vertical-align: top;color:#222; text-align:left; padding:7px 9px 7px 9px; border-top:1px solid #eee'>Tên thiết bị: <span style='color:red'>*</span></th>"
//						+ "          <td style='vertical-align:top;color:#333;width:60%;padding:7px 9px 7px 0;border-top:1px solid #eee'>"
//						+ device_name + "</td>" + "</tr>"
//
//						+ "<tr style='background-color:#f5f5f5'>"
//						+ "  <th style='vertical-align:top; color:#222; text-align:left; padding:7px 9px 7px 9px; border-top:1px solid #eee'>Địa điểm đặt thiết bị: <span style='color:red'>*</span></th>"
//						+ "          <td style='vertical-align:top;color:#333;width:60%;padding:7px 9px 7px 0;border-top:1px solid #eee'>"
//						+ nameDepartPlace + "</td>" + "</tr>"
//
//						+ "<tr style=''>"
//						+ "  <th style='vertical-align: top;color:#222; text-align:left; padding:7px 9px 7px 9px; border-top:1px solid #eee'>Nội dung yêu cầu: <span style='color:red'>*</span></th>"
//						+ "          <td style='vertical-align:top;color:#333;width:60%;padding:7px 9px 7px 0;border-top:1px solid #eee'>"
//						+ content_task + "</td>" + "</tr>"
//
//						+ "<tr style='background-color:#f5f5f5'>"
//						+ "  <th style='vertical-align:top; color:#222; text-align:left; padding:7px 9px 7px 9px; border-top:1px solid #eee'>Lý do: <span style='color:red'>*</span></th>"
//						+ "          <td style='vertical-align:top;color:#333;width:60%;padding:7px 9px 7px 0;border-top:1px solid #eee'>"
//						+ reason + "</td>" + "</tr>"
//
//						+ "</table>" + "</div>" + "<p><b style='font-size: 20px;'>Vui lòng nhấn vào <a href=\""
//						+ serverPublic + "/REQUEST_WEB/activated/confirmrepairrequestnotcd.jsf?idmem="
//						+ encodeStrCreateById + "&idcv=" + encodeStrIdcv + "&contenttask=" + encodeStrContentTast
//						+ "&contentcomplete=" + encodeStrContentComplete + "&createdate=" + encodeStrCreateDate
//						+ "&requesttypeid=" + encodeStrRequestTypeId + "&requestbyid=" + encodeStrRequestById + "&cn="
//						+ encodeStrDatabase + "\">đây</a> để xác nhận yêu cầu.</b></p>"
//						+ "<p style='font-size: 20px;'>Trân trọng và cảm ơn.</p>"
//						+ "<p style='font-size: 20px;'>Chức năng tạo phiếu sửa chữa của Công ty.</p>";
//
//				MimeBodyPart htmlPart = new MimeBodyPart();
//				htmlPart.setContent(htmlText, "text/html; charset=utf-8");
//
//				multipart.addBodyPart(textPart);
//				multipart.addBodyPart(htmlPart);
//				msg.setContent(multipart);
//
//				msg.setHeader("X-Mailer", "LOTONtechEmail");
//				msg.setSentDate(new Date());
//				msg.saveChanges();
//
//				msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
//				Transport.send(msg);
//			} else {
////				notify.warning("Người dùng không tồn tại!");
//			}
//
//			return true;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//}
