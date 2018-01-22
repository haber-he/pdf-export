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
import java.util.Arrays;
import java.util.List;

/**
 * Created by haber on 2017/4/11.
 */
public class PDFExportImage {


    private static final List excludePage = Arrays.asList(0);
    private static final boolean isCut = true;

    public static void setup(String filePath, String outDirPath) throws IOException {


        File file = new File(filePath);
        PDDocument document = PDDocument.load(file);
        PDFRenderer renderer = new PDFRenderer(document);

        int pageTotal = document.getNumberOfPages();
        System.out.println("页数：" + pageTotal);


        File outDir = new File(outDirPath);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        if (!outDir.isDirectory()) {
            System.err.println("请填写正确的输出路径");

            System.exit(0);
        }

        int pageName = 0;
        for (int pageIndex = 0; pageIndex < pageTotal; pageIndex++) {
            System.out.println("正在转换第 " + pageIndex + " 页");

            BufferedImage image = renderer.renderImageWithDPI(pageIndex, 400, ImageType.RGB);

            if (!isCut && pageIndex != (pageTotal - 1) && !excludePage.contains(pageIndex)) {
                String fileName1 = outDir + "/" + file.getName() + "-" + (pageName++) + ".jpg";
                cut(image, fileName1, 0, 0, image.getWidth() / 2, image.getHeight());

                String fileName2 = outDir + "/" + file.getName() + "-" + (pageName++) + ".jpg";
                cut(image, fileName2, image.getWidth() / 2, 0, image.getWidth() / 2, image.getHeight());

            } else {

                String fileName = outDir + "/" + file.getName() + "-" + (pageName++) + ".jpg";
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

    /**
     *
     * @param pics
     * @param type   1 横向拼接， 2 纵向拼接
     * @param dst_pic
     * @return
     */
    /**
     * @Description:图片拼接 （注意：必须两张图片长宽一致哦）
     * @author:liuyc
     * @time:2016年5月27日 下午5:52:24
     * @param files 要拼接的文件列表
     * @param type 1  横向拼接， 2 纵向拼接
     */
    public static void mergeImage(String[] files, int type, String targetFile) {
        int len = files.length;
        if (len < 1) {
            throw new RuntimeException("图片数量小于1");
        }
        File[] src = new File[len];
        BufferedImage[] images = new BufferedImage[len];
        int[][] ImageArrays = new int[len][];
        for (int i = 0; i < len; i++) {
            try {
                src[i] = new File(files[i]);
                images[i] = ImageIO.read(src[i]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            int width = images[i].getWidth();
            int height = images[i].getHeight();
            ImageArrays[i] = new int[width * height];
            ImageArrays[i] = images[i].getRGB(0, 0, width, height, ImageArrays[i], 0, width);
        }
        int newHeight = 0;
        int newWidth = 0;
        for (int i = 0; i < images.length; i++) {
            // 横向
            if (type == 1) {
                newHeight = newHeight > images[i].getHeight() ? newHeight : images[i].getHeight();
                newWidth += images[i].getWidth();
            } else if (type == 2) {// 纵向
                newWidth = newWidth > images[i].getWidth() ? newWidth : images[i].getWidth();
                newHeight += images[i].getHeight();
            }
        }
        if (type == 1 && newWidth < 1) {
            return;
        }
        if (type == 2 && newHeight < 1) {
            return;
        }

        // 生成新图片
        try {
            BufferedImage ImageNew = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            int height_i = 0;
            int width_i = 0;
            for (int i = 0; i < images.length; i++) {
                if (type == 1) {
                    ImageNew.setRGB(width_i, 0, images[i].getWidth(), newHeight, ImageArrays[i], 0,
                            images[i].getWidth());
                    width_i += images[i].getWidth();
                } else if (type == 2) {
                    ImageNew.setRGB(0, height_i, newWidth, images[i].getHeight(), ImageArrays[i], 0, newWidth);
                    height_i += images[i].getHeight();
                }
            }
            //输出想要的图片
            ImageIO.write(ImageNew, "jpg", new File(targetFile));
            System.out.println("finished.");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        String outDirPath = "/Users/haber/Documents/church/礼仪周刊/乙年-2018/常年期第三主日";
        String filePath = outDirPath + "/20180121常年期第三主日周刊曲线版.pdf";


//        String filePath = "/Users/haber/Documents/church/礼仪周刊/乙年-2018/圣家节/";
//        String[] fileNames = new String[]{filePath+"20171231圣家节周刊曲线版.pdf-9.jpg",filePath+"20171231圣家节周刊曲线版.pdf-10.jpg"};
//        mergeImage(fileNames, 1, filePath + "20171231圣家节周刊曲线版.pdf-9-0.jpg");

        setup(filePath, outDirPath);
//        BufferedImage bi = ImageIO.read(new File("/Users/haber/Downloads/test.jpg"));
//        cut(bi, outDir + "/test-1.jpg",0 , 0, bi.getWidth()/2, bi.getHeight());
//        cut(bi, outDir + "/test-2.jpg",bi.getWidth()/2 , 0, bi.getWidth()/2, bi.getHeight());
    }
}
