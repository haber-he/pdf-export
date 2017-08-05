package com.haber.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;

/**
 * Created by haber on 2017/4/11.
 */
public class PDFExportImage {
    public static final String filePath = "/Users/haber/Documents/church/礼仪周刊/甲年-2017/常年期第十七主日/20170730常年期第十七主日周刊.pdf";
    private  static final File outDir = new File("/Users/haber/Documents/church/礼仪周刊/甲年-2017/常年期第十七主日");
        public   static   void  setup()  throws IOException {


            File file = new File(filePath);
            PDDocument document = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(document);

            int pageNumber = document.getNumberOfPages();
            System.out.println("页数："+ pageNumber);


            if(!outDir.exists()) {
                outDir.mkdirs();
            }

            if(!outDir.isDirectory()) {
                System.err.println("请填写正确的输出路径");

                System.exit(0);
            }

            for (int pageIndex = 0; pageIndex < pageNumber; pageIndex++) {
                System.out.println("正在转换第 " + pageIndex + " 页");

                BufferedImage image = renderer.renderImageWithDPI(pageIndex, 300, ImageType.RGB);


                if(pageIndex != 0 && pageIndex != (pageNumber -1) ) {
                    String fileName1 = outDir + "/" + file.getName() + "-" + ((pageIndex - 1) * 2 + 1) + ".jpg";
                    cut(image, fileName1, 0, 0, image.getWidth() / 2, image.getHeight());

                    String fileName2 = outDir + "/" + file.getName() + "-" + ((pageIndex - 1) * 2 + 2) + ".jpg";
                    cut(image, fileName2, image.getWidth() / 2, 0, image.getWidth() / 2, image.getHeight());

                } else {

                    String fileName = outDir + "/" + file.getName() + "-" + (pageIndex * 2) + ".jpg";
                    ImageIO.write(image, "jpg", new File(fileName));
                }

            }

            document.close();

        }

    public final static void cut(BufferedImage bufferedImage, String result,
                                 int x, int y, int width, int height) {
        try {
            // 读取源图像
            int srcWidth = bufferedImage.getWidth(); // 源图宽度
            int srcHeight = bufferedImage.getHeight(); // 源图高度
            if (srcWidth > 0 && srcHeight > 0) {
                Image image = bufferedImage.getScaledInstance(srcWidth, srcHeight,
                        Image.SCALE_DEFAULT);
                // 四个参数分别为图像起点坐标和宽高
                // 即: CropImageFilter(int x,int y,int width,int height)
                ImageFilter cropFilter = new CropImageFilter(x, y, width, height);
                Image img = Toolkit.getDefaultToolkit().createImage(
                        new FilteredImageSource(image.getSource(),
                                cropFilter));
                BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics g = tag.getGraphics();
                g.drawImage(img, 0, 0, width, height, null); // 绘制切割后的图
                g.dispose();
                // 输出为文件
                ImageIO.write(tag, "jpg", new File(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        setup();
//        BufferedImage bi = ImageIO.read(new File("/Users/haber/Downloads/test.jpg"));
//        cut(bi, outDir + "/test-1.jpg",0 , 0, bi.getWidth()/2, bi.getHeight());
//        cut(bi, outDir + "/test-2.jpg",bi.getWidth()/2 , 0, bi.getWidth()/2, bi.getHeight());
    }
}
