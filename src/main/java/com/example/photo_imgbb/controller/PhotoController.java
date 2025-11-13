package com.example.photo_imgbb.controller;

import com.example.photo_imgbb.entity.Photo;
import com.example.photo_imgbb.repository.PhotoRepository;
import com.example.photo_imgbb.service.ImgbbService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {

    @Autowired
    private ImgbbService imgbbService;

    @Autowired
    private PhotoRepository photoRepository;

    @PostMapping("/upload")
    public Map<String, Object> uploadPhoto(@RequestParam("file") MultipartFile file) throws IOException, WriterException {
        Map<String, Object> uploadResult = imgbbService.upload(file.getBytes(), file.getOriginalFilename());

        // Trích xuất thông tin từ response imgbb
        Map data = (Map) uploadResult.get("data");
        String imageUrl = (String) data.get("display_url");
        //String imageUrl = (String) data.get("url");
        String deleteUrl = (String) data.get("delete_url");

        Photo photo = new Photo();
        photo.setUrl(imageUrl);
        photo.setPublicId(deleteUrl); // dùng delete_url như publicId
        photoRepository.save(photo);

        String url = "https://be-springboot-chuphinh-omo-production.up.railway.app/api/photos/view/" + photo.getId();

        String qrBase64 = generateQRCode(url);

        return Map.of(
                "url", url,
                "qrCode", qrBase64
        );
    }

    private String generateQRCode(String text) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        var bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    @GetMapping("/view/{photoId}")
    @ResponseBody
    public String viewPhoto(@PathVariable Long photoId) {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Photo not found"));

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<title>View Photo</title>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "body { display:flex; flex-direction:column; justify-content:center; align-items:center; min-height:100vh; margin:0; font-family:Arial,sans-serif; }" +
                "img { max-width:90%; height:auto; margin-bottom:20px; border-radius:10px; }" +
                "h4 { color:#333; font-size:18px; text-align:center; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<img src='" + photo.getUrl() + "' alt='Photo' />" +
                "<h4>Hãy ấn giữ ảnh để tải ảnh về máy</h4>" +
                "</body>" +
                "</html>";
    }

    @GetMapping("/download/{photoId}")
    public void downloadPhoto(@PathVariable Long photoId, HttpServletResponse response) throws IOException {
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Photo not found"));

        java.net.URL url = new java.net.URL(photo.getUrl());
        var connection = url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        try (var in = connection.getInputStream()) {
            response.setContentType("image/png");
            response.setHeader("Content-Disposition", "attachment; filename=\"photo.png\"");
            in.transferTo(response.getOutputStream());
        }
    }

    @GetMapping("/all")
    @ResponseBody
    public String viewAllPhotos() {
        var photos = photoRepository.findAll();

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>")
                .append("<html>")
                .append("<head>")
                .append("<title>All Photos</title>")
                .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; margin:0; padding:20px; background:#f5f5f5; }")
                .append(".gallery { display:grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap:15px; }")
                .append(".gallery img { width:100%; height:auto; border-radius:10px; box-shadow:0 2px 8px rgba(0,0,0,0.15); cursor:pointer; transition: transform 0.2s; }")
                .append(".gallery img:hover { transform: scale(1.05); }")
                .append("h2 { text-align:center; margin-bottom:20px; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<h2>Danh sách tất cả ảnh đã upload</h2>")
                .append("<div class='gallery'>");

        for (Photo photo : photos) {
            html.append("<a href='/api/photos/view/").append(photo.getId()).append("'>")
                    .append("<img src='").append(photo.getUrl()).append("' alt='Photo'/>")
                    .append("</a>");
        }

        html.append("</div>")
                .append("</body>")
                .append("</html>");

        return html.toString();
    }

    @GetMapping("/getall")
    public Map<String, Object> getAllPhotos() {
        var photos = photoRepository.findAll();
        return Map.of(
                "count", photos.size(),
                "photos", photos
        );
    }

    @DeleteMapping("/delete-all")
    public Map<String, Object> deleteAllPhotos() {
        photoRepository.deleteAll();
        return Map.of(
                "message", "Đã xoá toàn bộ ảnh trong database"
        );
    }
}
