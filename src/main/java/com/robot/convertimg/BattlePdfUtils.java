package com.robot.convertimg;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Michael
 */
public class BattlePdfUtils {

    private static final String IMG_TYPE = "jpg";
    private static final String filePattern = ".pdf" ;
    /**生成的新文件路径*/
    private static final String newPDFPath = "/data/resources/output/";
    /**图片访问url*/
    private static final String url = "http://106.12.174.25:8080/pictures/battle/";
    /**生成图片文件路径*/
    private static final String outputDir = "/data/jonas/server/apache-tomcat-7.0.68/webapps/pictures/battle/";

    /**
     *  利用模板生成pdf
     */
    public static String pdfOutForBattle(Map<String,Object> o) {
        // 模板路径
        String templatePath = "/data/resources/splatoon2_pictures/form/form_battle.pdf";

        PdfReader reader;
        FileOutputStream out;
        ByteArrayOutputStream bos;
        PdfStamper stamper;
        try {
            BaseFont bf = BaseFont.createFont("/data/resources/font/Futura-Bold-5.ttf" , BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            BaseFont bf1 = BaseFont.createFont("/data/resources/font/4080_funder_black_GBK.ttf" , BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            String fileName = System.currentTimeMillis() + UUID.randomUUID().toString().substring(0,4);
            // 输出流
            out = new FileOutputStream(newPDFPath + fileName + filePattern);
            // 读取pdf模板
            reader = new PdfReader(templatePath);
            bos = new ByteArrayOutputStream();
            stamper = new PdfStamper(reader, bos);
            AcroFields form = stamper.getAcroFields();
            //文字类的内容处理
            Map<String,String> textWhiteMap = (Map<String,String>)o.get("textWhiteMap");
            form.addSubstitutionFont(bf);
            form.addSubstitutionFont(bf1);
            for(String key : textWhiteMap.keySet()){
                String value = textWhiteMap.get(key);
                form.setFieldProperty(key,"textsize",23f,null);
                form.setFieldProperty(key,"textcolor", BaseColor.WHITE,null);
                form.setField(key,value);
            }

            Map<String,String> textTimeMap = (Map<String,String>)o.get("textTimeMap");
            for (String key:textTimeMap.keySet()) {
                String value = textTimeMap.get(key);
                form.setFieldProperty(key,"textsize",19f,null);
                form.setFieldProperty(key,"textcolor", BaseColor.WHITE,null);
                form.setField(key,value);
            }

            //图片类的内容处理
            Map<String,String> imgmap = (Map<String,String>)o.get("imgMap");
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

    public static String getBattleImg(Map<String,String> regularMap,
                                      Map<String,String> rankMap,Map<String,String> leagueMap,String time) {
        Map<String,String> textWhiteMap = new HashMap();

        textWhiteMap.put("regular_white_1", regularMap.get("mode").charAt(0) + "");
        textWhiteMap.put("regular_white_2", regularMap.get("mode").charAt(1) + "");

        textWhiteMap.put("rank_white_1", rankMap.get("mode").charAt(0) + "");
        textWhiteMap.put("rank_white_2", rankMap.get("mode").charAt(1) + "");

        textWhiteMap.put("league_white_1", leagueMap.get("mode").charAt(0) + "");
        textWhiteMap.put("league_white_2", leagueMap.get("mode").charAt(1) + "");

        Map<String,String> textTimeMap = new HashMap();

        textTimeMap.put("time",time);


        Map<String,String> picMap = new HashMap();
        String stagePath = "/data/resources/splatoon2_pictures/stages/";

        picMap.put("regular_map1",stagePath + regularMap.get("map1") + ".png");
        picMap.put("regular_map2",stagePath + regularMap.get("map2") + ".png");

        picMap.put("rank_map1",stagePath + rankMap.get("map1") + ".png");
        picMap.put("rank_map2",stagePath + rankMap.get("map2") + ".png");

        picMap.put("league_map1",stagePath + leagueMap.get("map1") + ".png");
        picMap.put("league_map2",stagePath + leagueMap.get("map2") + ".png");

        Map<String,Object> o=new HashMap();
        o.put("textWhiteMap",textWhiteMap);
        o.put("textTimeMap",textTimeMap);

        o.put("imgMap",picMap);
        return pdfOutForBattle(o);
    }
}
