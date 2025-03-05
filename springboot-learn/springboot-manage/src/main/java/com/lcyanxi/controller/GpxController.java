package com.lcyanxi.controller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class GpxController {
    public static void main(String[] args) {
        // GPX 文件路径
        String gpxFilePath = "/Users/Keep/Downloads/1726719796206_11834970.gpx"; // 替换为实际的文件路径

        // 解析 GPX 文件
        try {
            List<TrackPoint> trackPoints = parseGpxFile(gpxFilePath);
            trackPoints.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<TrackPoint> parseGpxFile(String filePath) throws Exception {
        final String GPX_NAMESPACE = "http://www.topografix.com/GPX/1/1";
        List<TrackPoint> points = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(filePath));

        NodeList trkptNodes = doc.getElementsByTagNameNS(GPX_NAMESPACE, "trkpt");

        for (int i = 0; i < trkptNodes.getLength(); i++) {
            Element trkptElement = (Element) trkptNodes.item(i);

            // 解析经纬度
            double lat = Double.parseDouble(trkptElement.getAttribute("lat"));
            double lon = Double.parseDouble(trkptElement.getAttribute("lon"));

            // 解析高程
            Double elevation = null;
            NodeList eleNodes = trkptElement.getElementsByTagNameNS(GPX_NAMESPACE, "ele");
            if (eleNodes.getLength() > 0) {
                elevation = Double.parseDouble(eleNodes.item(0).getTextContent());
            }

            // 解析时间
            Instant time = null;
            NodeList timeNodes = trkptElement.getElementsByTagNameNS(GPX_NAMESPACE, "time");
            if (timeNodes.getLength() > 0) {
                String timeString = timeNodes.item(0).getTextContent();
                time = Instant.parse(timeString);
            }

            points.add(new TrackPoint(lat, lon, elevation, time));
        }

        return points;
    }

    static  class TrackPoint {
        private double latitude;
        private double longitude;
        private Double elevation;
        private Instant time;

        public TrackPoint(double latitude, double longitude, Double elevation, Instant time) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.elevation = elevation;
            this.time = time;
        }

        // Getters
        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public Double getElevation() {
            return elevation;
        }

        public Instant getTime() {
            return time;
        }

        @Override
        public String toString() {
            return String.format("TrackPoint{lat=%.6f, lon=%.6f, ele=%s, time=%s}", latitude, longitude, elevation, time);
        }
    }
}
