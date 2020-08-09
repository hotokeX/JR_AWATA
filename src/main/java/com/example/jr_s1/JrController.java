package com.example.jr_s1;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@RestController
public class JrController {

  /*
   * Getパラメータで帳票タイトルを受取り、指定ディレクトリに帳票を出力すると共にブラウザに表示します。
   * ・テストURL
   * http://localhost:8080/pdf_test_1?pdfTitle=帳票タイトル
   *
   */
  @GetMapping(path = "/pdf_test_1")
  public void outPutReport1(@Value("classpath:jasperreports/SampleT2_A4.jrxml") Resource jrxml,
  										@RequestParam String pdfTitle,
  										HttpServletResponse response) throws Exception {

		// jrxml オブジェクト
    JasperReport jasperReport;

    // 画像パス
    String imgPath = "src\\main\\resources\\img\\Graph_1.png";

    // 空のデータソースでデータソースを初期化
    JRDataSource dataSource = new JREmptyDataSource();

    // パラメータ設定用 Map
    Map<String, Object> params = new HashMap<>();

    // 画像とjrxmlファイル[帳票]の読込み
    try(InputStream imgsrc = new FileInputStream(imgPath); InputStream inputStream = jrxml.getInputStream()){

    	// パラメータ設定
      params.put("title", pdfTitle);
      params.put("imgsrc", imgsrc);

    	// jrxmlファイル[帳票]をコンパイル[.jasper]
    	jasperReport = JasperCompileManager.compileReport(inputStream);

    	// データソースを帳票にバインド
       JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

      ////////////////////////////////////////// ブラウザ出力
      // Servletレスポンスを PDF に設定
      response.setContentType(MediaType.APPLICATION_PDF_VALUE);

      // 出力ストリームに PDF を書き出し
      JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());


      ////////////////////////////////////////// ディレクトリ出力
      String outputPdfPath = "src\\main\\resources\\jasperreports\\out\\TOP_Blank_A4.pdf";

      // 対象パスに PDF を書き出し
  		JasperExportManager.exportReportToPdfFile(jasperPrint, outputPdfPath);

    }catch(Exception e) {
    	throw new Exception(e);
    }


  }
}
