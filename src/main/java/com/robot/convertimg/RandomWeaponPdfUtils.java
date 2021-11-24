package com.robot.convertimg;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.robot.common.CommonUtils;
import com.robot.enums.Mode;
import com.robot.enums.RankedMode;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;

import static javax.print.attribute.ResolutionSyntax.DPI;

public class RandomWeaponPdfUtils {

    private static final String IMG_TYPE = "jpg";
    private static final String filePattern = ".pdf" ;
    /**生成的新文件路径*/
    private static final String newPDFPath = "/data/resources/output/";
    /**图片访问url*/
    private static final String url = "http://106.12.174.25:8080/pictures/random_weapon/";
    /**
     *  利用模板生成pdf
     */
    public static String pdfOutForRandomWeapon(Map<String,Object> o) {
        // 模板路径
        String templatePath = "/data/resources/splatoon2_pictures/form/form_random_weapon.pdf";
        PdfReader reader;
        FileOutputStream out;
        ByteArrayOutputStream bos;
        PdfStamper stamper;
        try {
            BaseFont bf = BaseFont.createFont("/data/resources/font/SIMSUN.TTC,1" , BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font FontChinese = new Font(bf, 20, Font.NORMAL);
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
            Font font = new Font(bf, 32);
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
            String outputDir = "/data/jonas/server/apache-tomcat-7.0.68/webapps/pictures/random_weapon/";
            File file = new File(outputDir + fileName + "." + IMG_TYPE);
            for (int i = 0; i < document.getNumberOfPages(); ++i) {
                BufferedImage bufferedImage = renderer.renderImageWithDPI(i, DPI);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, IMG_TYPE, file);
            }
        }
    }

    public static String getRandomWeaponImg(Map<Integer,String> weaponDetailA,Map<Integer,String> weaponDetailB,Map<String,Object> mapDetail) {
        Map<String,String> textMap = new HashMap();
        //获取随机模式
        String mode = CommonUtils.getInstance().randomEnum(RankedMode.class).getChineseMode();
        textMap.put("mode_name",mode);

        //获取随机地图
        String mapName = (String) mapDetail.get("chineseName");
        textMap.put("map_name",mapName);

        Map<String,String> picMap = new HashMap();
        String stagePath = "/data/resources/splatoon2_pictures/stages/";
        String mainPath = "/data/resources/splatoon2_pictures/main/";
        String subPath = "/data/resources/splatoon2_pictures/sub/";
        String specialPath = "/data/resources/splatoon2_pictures/special/";
        picMap.put("map",stagePath + mapDetail.get("pictureName") + ".png");

        int i = 1;
        for (int key:weaponDetailA.keySet()) {
            String mainKey = "A" + i + "_main";
            String subKey = "A" + i + "_sub";
            String specialKey = "A" + i + "_special";
            String[] pictures = weaponDetailA.get(key).split(",");

            picMap.put(mainKey,mainPath + pictures[0] + ".png");
            picMap.put(subKey,subPath + pictures[1] + ".png");
            picMap.put(specialKey,specialPath + pictures[2] + ".png");
            i++;
        }

        i = 1;
        for (int key:weaponDetailB.keySet()) {
            String mainKey = "B" + i + "_main";
            String subKey = "B" + i + "_sub";
            String specialKey = "B" + i + "_special";
            String[] pictures = weaponDetailB.get(key).split(",");

            picMap.put(mainKey,mainPath + pictures[0] + ".png");
            picMap.put(subKey,subPath + pictures[1] + ".png");
            picMap.put(specialKey,specialPath + pictures[2] + ".png");
            i++;
        }

        Map<String,Object> o=new HashMap();
        o.put("datemap",textMap);
        o.put("imgmap",picMap);
        return pdfOutForRandomWeapon(o);
    }
}
