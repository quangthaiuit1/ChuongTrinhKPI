package trong.lixco.com.util;

import java.util.ArrayList;
import java.util.List;

import trong.lixco.com.classInfor.ChiTietKetQuaDanhGia360;
import trong.lixco.com.classInfor.KetQuaDanhGia360;

public class ToObjectFromClass {
	public static List<Object[]> toObjectKQ360Chitiet(List<ChiTietKetQuaDanhGia360> kq) {
		List<Object[]> results = new ArrayList<Object[]>();
		Object[] title = { "PHÒNG BAN", "MÃ NHÂN VIÊN", "TÊN NHÂN VIÊN", "ĐIỂM TỰ ĐÁNH GIÁ", "NV ĐÁNH GIÁ 1", "ĐIỂM 1",
				"NV ĐÁNH GIÁ 2", "ĐIỂM 2", "NV ĐÁNH GIÁ 3", "ĐIỂM 3", "NV ĐÁNH GIÁ 4", "ĐIỂM 4", "NV ĐÁNH GIÁ 5",
				"ĐIỂM 5", "ĐIỂM TRUNG BÌNH" };
		results.add(title);
		for (int i = 0; i < kq.size(); i++) {
			Object[] result = { kq.get(i).getPhongban(), kq.get(i).getManhanvien(), kq.get(i).getTennhanvien(),
					Math.round(kq.get(i).getDiemtudanhgia() * 10) / 10.0, kq.get(i).getNv1(),
					Math.round(kq.get(i).getDiem1() * 10) / 10.0, kq.get(i).getNv2(),
					Math.round(kq.get(i).getDiem2() * 10) / 10.0, kq.get(i).getNv3(),
					Math.round(kq.get(i).getDiem3() * 10) / 10.0, kq.get(i).getNv4(),
					Math.round(kq.get(i).getDiem4() * 10) / 10.0, kq.get(i).getNv5(),
					Math.round(kq.get(i).getDiem5() * 10) / 10.0, Math.round(kq.get(i).getDiemtrungbinh() * 10) / 10.0 };
			results.add(result);
		}
		return results;
	}

	public static List<Object[]> toObjectKQ360(List<KetQuaDanhGia360> kq) {
		List<Object[]> results = new ArrayList<Object[]>();
		Object[] title = { "PHÒNG BAN", "MÃ NHÂN VIÊN", "TÊN NHÂN VIÊN", "ĐIỂM KPI PHÒNG", "ĐIỂM KPI CÁ NHÂN",
				"ĐIỂM ĐÁNH GIÁ", "ĐIỂM THAM GIA PHONG TRÀO", "ĐIỂM TRỪ VI PHẠM", "ĐIỂM TỔNG" };
		results.add(title);
		for (int i = 0; i < kq.size(); i++) {
			Object[] result = { kq.get(i).getPhongban(), kq.get(i).getManhanvien(), kq.get(i).getTennhanvien(),
					Math.round(kq.get(i).getKpiphong() * 10) / 10.0, Math.round(kq.get(i).getKpicanhan() * 10) / 10.0,
					Math.round(kq.get(i).getDiemdanhgia() * 10) / 10.0,
					Math.round(kq.get(i).getDiemphongtrao() * 10) / 10.0,
					Math.round(kq.get(i).getDiemtruvipham() * 10) / 10.0,
					Math.round(kq.get(i).getTongdiem() * 10) / 10.0 };
			results.add(result);
		}
		return results;
	}

}
