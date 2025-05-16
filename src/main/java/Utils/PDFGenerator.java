package Utils;

import Models.Excursion;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PDFGenerator {

    /**
     * Génère un PDF avec les informations de l'excursion
     * @param excursion L'excursion dont on veut générer le PDF
     * @return Le chemin du fichier PDF généré
     * @throws Exception Si une erreur survient lors de la génération du PDF
     */
    public static String generateExcursionPDF(Excursion excursion) throws Exception {
        // Créer un dossier pour les PDF s'il n'existe pas
        File pdfDir = new File("pdf");
        if (!pdfDir.exists()) {
            pdfDir.mkdir();
        }

        // Créer un nom de fichier unique basé sur l'ID de l'excursion et la date actuelle
        String fileName = "excursion_" + excursion.getId_excursion() + "_" + System.currentTimeMillis() + ".pdf";
        String filePath = "pdf/" + fileName;

        // Créer le document PDF
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Ajouter un titre
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
        Paragraph title = new Paragraph("Détails de l'excursion: " + excursion.getTitre(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        // Ajouter une ligne de séparation
        LineSeparator line = new LineSeparator();
        document.add(line);
        document.add(Chunk.NEWLINE);

        // Créer une table pour les informations
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Définir les largeurs des colonnes
        float[] columnWidths = {1f, 3f};
        table.setWidths(columnWidths);

        // Style pour les en-têtes
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
        
        // Style pour le contenu
        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

        // Ajouter les informations à la table
        addTableRow(table, "Titre", excursion.getTitre(), headerFont, contentFont);
        addTableRow(table, "Destination", excursion.getDestination(), headerFont, contentFont);
        
        // Formater la date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateStr = dateFormat.format(excursion.getDate());
        addTableRow(table, "Date", dateStr, headerFont, contentFont);
        
        addTableRow(table, "Durée", excursion.getDuree() + " jour(s)", headerFont, contentFont);
        addTableRow(table, "Transport", excursion.getTransport(), headerFont, contentFont);
        
        // Ajouter l'événement associé
        String eventTitle = excursion.getEvent() != null ? excursion.getEvent().getTitle() : "Non spécifié";
        addTableRow(table, "Événement", eventTitle, headerFont, contentFont);
        
        // Ajouter l'organisateur
        String organizer = "";
        if (excursion.getUser() != null) {
            organizer = excursion.getUser().getPrenom() + " " + excursion.getUser().getNom();
        } else {
            organizer = "Non spécifié";
        }
        addTableRow(table, "Organisé par", organizer, headerFont, contentFont);


        // Ajouter la table au document
        document.add(table);
        
        // Ajouter une note de bas de page
        document.add(Chunk.NEWLINE);
        document.add(line);
        document.add(Chunk.NEWLINE);
        
        Font noteFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY);
        Paragraph note = new Paragraph("Document généré le " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), noteFont);
        note.setAlignment(Element.ALIGN_CENTER);
        document.add(note);

        // Fermer le document
        document.close();

        return filePath;
    }

    /**
     * Ajoute une ligne à la table PDF
     * @param table La table PDF
     * @param header Le titre de la ligne
     * @param content Le contenu de la ligne
     * @param headerFont La police pour le titre
     * @param contentFont La police pour le contenu
     */
    private static void addTableRow(PdfPTable table, String header, String content, Font headerFont, Font contentFont) {
        PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
        headerCell.setBackgroundColor(BaseColor.BLUE);
        headerCell.setPadding(5);
        
        PdfPCell contentCell = new PdfPCell(new Phrase(content, contentFont));
        contentCell.setPadding(5);
        
        table.addCell(headerCell);
        table.addCell(contentCell);
    }
}
