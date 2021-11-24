package com.robot.convertimg;

import com.itextpdf.text.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static javax.print.attribute.ResolutionSyntax.DPI;

/**
 * @author Michael
 */
public class SalmonRunPdfUtils {

    private static final String IMG_TYPE = "jpg";
    private static final String filePattern = ".pdf" ;
    /**生成的新文件路径*/
//    private static final String newPDFPath = "/Users/michael/Desktop/splatoon2_resources/splatoon2_pictures/output/";
    private static final String newPDFPath = "/data/resources/output/";
    /**图片访问url*/
    private static final String url = "http://106.12.174.25:8080/pictures/salmon_run/";
    /**生成图片文件路径*/
//    private static final String outputDir = "/Users/michael/Desktop/splatoon2_resources/splatoon2_pictures/output/";
    private static final String outputDir = "/data/jonas/server/apache-tomcat-7.0.68/webapps/pictures/salmon_run/";

    /**
     *  利用模板生成pdf
     */
    public static String pdfOutForSalmonRun(Map<String,Object> o) {
        // 模板路径
//        String templatePath = "/Users/michael/Desktop/splatoon2_resources/splatoon2_pictures/form/form_salmon_run.pdf";
        String templatePath = "/data/resources/splatoon2_pictures/form/form_salmon_run.pdf";

        PdfReader reader;
        FileOutputStream out;
        ByteArrayOutputStream bos;
        PdfStamper stamper;
        try {
//            BaseFont bf = BaseFont.createFont("/Users/michael/Desktop/splatoon2_resources/splatoon2_pictures/font/Futura-Bold-5.ttf" , BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            BaseFont bf = BaseFont.createFont("/data/resources/font/Futura-Bold-5.ttf" , BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            String fileName = System.currentTimeMillis() + UUID.randomUUID().toString().substring(0,4);
            // 输出流
            out = new FileOutputStream(newPDFPath + fileName + filePattern);
            // 读取pdf模板
            reader = new PdfReader(templatePath);
            bos = new ByteArrayOutputStream();
            stamper = new PdfStamper(reader, bos);
            AcroFields form = stamper.getAcroFields();
            //文字类的内容处理
            Map<String,String> datemap = (Map<String,String>)o.get("datemap");
            form.addSubstitutionFont(bf);
            for(String key : datemap.keySet()){
                String value = datemap.get(key);
                form.setFieldProperty(key,"textsize",19f,null);
                form.setFieldProperty(key,"textcolor", BaseColor.WHITE,null);
                form.setField(key,value);
            }
            //图片类的内容处理
            Map<String,String> imgmap = (Map<String,String>)o.get("imgmap");
            for(String key : imgmap.keySet()) {
                String value = imgmap.get(key);
                String imgpath = value;
                int pageNo = form.getFieldPositions(key).get(0).page;
                Rectangle signRect = form.getFieldPositions(key).get(0).position;
                float x = signRect.getLeft();
                float y = signRect.getBottom();
                //根据路径读取图片
                Image image = Image.getInstance(imgpath);
                //获取图片页面
                PdfContentByte under = stamper.getOverContent(pageNo);
                //图片大小自适应
                image.scaleToFit(signRect.getWidth(), signRect.getHeight());
                //添加图片
                image.setAbsolutePosition(x, y);
                under.addImage(image);
            }
            // 如果为false，生成的PDF文件可以编辑，如果为true，生成的PDF文件不可以编辑
            stamper.setFormFlattening(true);
            stamper.close();
            Document doc = new Document();
            PdfCopy copy = new PdfCopy(doc, out);
            doc.open();
            PdfImportedPage importPage = copy.getImportedPage(new PdfReader(bos.toByteArray()), 1);
            copy.addPage(importPage);
            doc.close();

            pdfToImage(fileName);

            return url + fileName + "." + IMG_TYPE;
        } catch (IOException e) {
            System.out.println(e);
        } catch (DocumentException e) {
            System.out.println(e);
        }
        return "";
    }

    /**
     * PDF转图片
     *
     * @return 图片文件的二进制流
     */
    public static void pdfToImage(String fileName) throws IOException {
        try (PDDocument document = PDDocument.load(new File(newPDFPath + fileName + filePattern))) {
            PDFRenderer renderer = new PDFRenderer(document);

            File file = new File(outputDir + fileName + "." + IMG_TYPE);
            for (int i = 0; i < document.getNumberOfPages(); ++i) {
                BufferedImage bufferedImage = renderer.renderImageWithDPI(i, 150);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, IMG_TYPE, file);
            }
        }
    }

    public static String getSalmonRunImg(Map<String,String> m1Map,Map<String,String> m2Map) {
        Map<String,String> textMap = new HashMap();
        textMap.put("m1_time",m1Map.get("time"));
        textMap.put("m2_time",m2Map.get("time"));

        Map<String,String> picMap = new HashMap();
        String stagePath = "/data/resources/splatoon2_pictures/work/";
        String mainPath = "/data/resources/splatoon2_pictures/main/";

//        String stagePath = "/Users/michael/Desktop/splatoon2_resources/splatoon2_pictures/work/";
//        String mainPath = "/Users/michael/Desktop/splatoon2_resources/splatoon2_pictures/main/";

        picMap.put("map1",stagePath + m1Map.get("map"));
        picMap.put("map2",stagePath + m2Map.get("map"));

        picMap.put("m1_weapon1",mainPath + m1Map.get("weapon1") + ".png");
        picMap.put("m1_weapon2",mainPath + m1Map.get("weapon2") + ".png");
        picMap.put("m1_weapon3",mainPath + m1Map.get("weapon3") + ".png");
        picMap.put("m1_weapon4",mainPath + m1Map.get("weapon4") + ".png");

        picMap.put("m2_weapon1",mainPath + m2Map.get("weapon1") + ".png");
        picMap.put("m2_weapon2",mainPath + m2Map.get("weapon2") + ".png");
        picMap.put("m2_weapon3",mainPath + m2Map.get("weapon3") + ".png");
        picMap.put("m2_weapon4",mainPath + m2Map.get("weapon4") + ".png");

        Map<String,Object> o=new HashMap();
        o.put("datemap",textMap);
        o.put("imgmap",picMap);
        return pdfOutForSalmonRun(o);
    }
}
