package com.example.study.simple.code.resizeImg;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public abstract class ImageProcessingService {
    public enum SupportedExtensionType {
        JPEG("jpeg"),
        PNG("png"),
        JPG("jpg"),
        GIF("gif"),
        AVI("avi"),
        MP4("mp4");

        private String extension;

        SupportedExtensionType(String extension) {
            this.extension = extension;
        }

        public String getExtension() {
            return extension;
        }

        public static boolean isGif(String input) {
            return GIF.getExtension().equalsIgnoreCase(input);
        }

        public static boolean isSupport(String input) {
            return Arrays.stream(values()).anyMatch(extension -> extension.getExtension().equalsIgnoreCase(input));
        }

        public static boolean isImageExtension(String input) {
            return !(AVI.getExtension().equalsIgnoreCase(input) || MP4.getExtension().equalsIgnoreCase(input)) ? true : false;
        }
    }

    public static void resizeImage(File input, File output, int width) throws IOException {
        Image img = new ImageIcon(input.toString()).getImage();

        int originWidth = img.getWidth(null);
        int originHeight = img.getHeight(null);

        int height = (originHeight * width) / originWidth;

        resizeImage(input, output, width, height);
    }

    public static void resizeImage(File input, File output, int width, int height) throws IOException {
        String extension = Optional.of(input.getName())
                .filter(name -> name.contains("."))
                .map(name -> name.substring(name.lastIndexOf(".") + 1))
                .orElseThrow(() -> new RuntimeException("파일 확장자 확인불가"));

        if (!SupportedExtensionType.isImageExtension(extension)) {
            throw new RuntimeException("지원하지 않는 이미지 확장자");
        }

        InputStream inputStream = new FileInputStream(input);
        BufferedImage inputImage = ImageIO.read(inputStream);
        BufferedImage outputImage = new BufferedImage(width, height, inputImage.getType());

        Graphics2D graphics2D = outputImage.createGraphics();
        graphics2D.drawImage(inputImage, 0, 0, width, height, null);
        graphics2D.dispose();

        ImageIO.write(outputImage, extension, output);
    }

    public static void resizeGif(File input, File output, int width, int height) throws IOException {
        String extension = Optional.of(input.getName())
                .filter(name -> name.contains("."))
                .map(name -> name.substring(name.lastIndexOf(".") + 1))
                .orElseThrow(() -> new RuntimeException("파일 확장자 확인불가"));

        if (!SupportedExtensionType.isGif(extension)) {
            throw new RuntimeException("gif 아님");
        }

        ImageFrame[] imageFrames = readGif(input);

        ImageFrame[] resizedFrames = new ImageFrame[imageFrames.length];

        for (int i = 0; i < imageFrames.length; i++) {
            resizedFrames[i] = ImageProcessingService.resizeImage(imageFrames[i], width, height);
        }

        ImageOutputStream outputStream = new FileImageOutputStream(output);
        GifSequenceWriter writer = new GifSequenceWriter(outputStream, resizedFrames[0].getImage().getType(), resizedFrames[0].getDelay(), true);

        writer.writeToSequence(resizedFrames[0].getImage());

        for ( int i = 1; i < imageFrames.length; i++ ) {
            BufferedImage nextImage = resizedFrames[i].getImage();
            writer.writeToSequence( nextImage );
        }

        writer.close();
        outputStream.close();

    }

    public static ImageFrame resizeImage(ImageFrame input, int width, int height) throws IOException {
        BufferedImage inputImage = input.getImage();
        BufferedImage outputImage = new BufferedImage(width, height, inputImage.getType());

        Graphics2D graphics2D = outputImage.createGraphics();
        graphics2D.drawImage(inputImage, 0, 0, width, height, null);
        graphics2D.dispose();

        return new ImageFrame(outputImage, input.getDelay(), input.getDisposal(), width, height);
    }

    public static ImageFrame[] readGif(File input) throws IOException {
        InputStream inputStream = new FileInputStream(input);
        return readGif(inputStream);
    }

    private static ImageFrame[] readGif(InputStream stream) throws IOException {
        ArrayList<ImageFrame> frames = new ArrayList<>(2);

        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        reader.setInput(ImageIO.createImageInputStream(stream));

        int lastx = 0;
        int lasty = 0;

        int width = -1;
        int height = -1;

        IIOMetadata metadata = reader.getStreamMetadata();

        Color backgroundColor = null;

        if(metadata != null) {
            IIOMetadataNode globalRoot = (IIOMetadataNode) metadata.getAsTree(metadata.getNativeMetadataFormatName());

            NodeList globalColorTable = globalRoot.getElementsByTagName("GlobalColorTable");
            NodeList globalScreeDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");

            if (globalScreeDescriptor.getLength() > 0) {
                IIOMetadataNode screenDescriptor = (IIOMetadataNode) globalScreeDescriptor.item(0);

                if (screenDescriptor != null) {
                    width = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth"));
                    height = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight"));
                }
            }

            if (globalColorTable.getLength() > 0){
                IIOMetadataNode colorTable = (IIOMetadataNode) globalColorTable.item(0);

                if (colorTable != null) {
                    String bgIndex = colorTable.getAttribute("backgroundColorIndex");

                    IIOMetadataNode colorEntry = (IIOMetadataNode) colorTable.getFirstChild();
                    while (colorEntry != null) {
                        if (colorEntry.getAttribute("index").equals(bgIndex)) {
                            int red = Integer.parseInt(colorEntry.getAttribute("red"));
                            int green = Integer.parseInt(colorEntry.getAttribute("green"));
                            int blue = Integer.parseInt(colorEntry.getAttribute("blue"));

                            backgroundColor = new Color(red, green, blue);
                            break;
                        }

                        colorEntry = (IIOMetadataNode) colorEntry.getNextSibling();
                    }
                }
            }
        }

        BufferedImage master = null;
        boolean hasBackround = false;

        for (int frameIndex = 0; ; frameIndex++) {
            BufferedImage image;
            try{
                image = reader.read(frameIndex);
            }catch (IndexOutOfBoundsException io){
                break;
            }

            if (width == -1 || height == -1){
                width = image.getWidth();
                height = image.getHeight();
            }

            IIOMetadataNode root = (IIOMetadataNode) reader.getImageMetadata(frameIndex).getAsTree("javax_imageio_gif_image_1.0");
            IIOMetadataNode gce = (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension").item(0);
            NodeList children = root.getChildNodes();

            int delay = Integer.parseInt(gce.getAttribute("delayTime"));

            String disposal = gce.getAttribute("disposalMethod");

            if (master == null) {
                master = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                master.createGraphics().setColor(backgroundColor);
                master.createGraphics().fillRect(0, 0, master.getWidth(), master.getHeight());

                hasBackround = image.getWidth() == width && image.getHeight() == height;

                master.createGraphics().drawImage(image, 0, 0, null);
            } else {
                int x = 0;
                int y = 0;

                for (int nodeIndex = 0; nodeIndex < children.getLength(); nodeIndex++){
                    Node nodeItem = children.item(nodeIndex);

                    if (nodeItem.getNodeName().equals("ImageDescriptor")){
                        NamedNodeMap map = nodeItem.getAttributes();

                        x = Integer.parseInt(map.getNamedItem("imageLeftPosition").getNodeValue());
                        y = Integer.parseInt(map.getNamedItem("imageTopPosition").getNodeValue());
                    }
                }

                if (disposal.equals("restoreToPrevious")){
                    BufferedImage from = null;
                    for (int i = frameIndex - 1; i >= 0; i--){
                        if (!frames.get(i).getDisposal().equals("restoreToPrevious") || frameIndex == 0){
                            from = frames.get(i).getImage();
                            break;
                        }
                    }

                    assert from != null;
                    ColorModel model = from.getColorModel();
                    boolean alpha = from.isAlphaPremultiplied();
                    WritableRaster raster = from.copyData(null);
                    master = new BufferedImage(model, raster, alpha, null);
                } else if (disposal.equals("restoreToBackgroundColor") && backgroundColor != null) {
                    if (!hasBackround || frameIndex > 1) {
                        master.createGraphics().fillRect(lastx, lasty, frames.get(frameIndex - 1).getWidth(), frames.get(frameIndex - 1).getHeight());
                    }
                }
                master.createGraphics().drawImage(image, x, y, null);

                lastx = x;
                lasty = y;
            }

            ColorModel model = master.getColorModel();
            boolean alpha = master.isAlphaPremultiplied();
            WritableRaster raster = master.copyData(null);
            BufferedImage copy = new BufferedImage(model, raster, alpha, null);

            frames.add(new ImageFrame(copy, delay, disposal, image.getWidth(), image.getHeight()));

            master.flush();
        }
        reader.dispose();

        return frames.toArray(new ImageFrame[frames.size()]);
    }

}
