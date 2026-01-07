package cz.reservation.service.invoice;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import cz.reservation.entity.InvoiceSummaryEntity;
import cz.reservation.entity.PackageEntity;
import cz.reservation.entity.PlayerEntity;
import cz.reservation.service.serviceinterface.CompanyInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InvoiceEngineImpl implements InvoiceEngine {


    private final String path;
    private final CompanyInfoService companyInfoService;

    private static final String DATE_PATTERN = "dd.MM.yyyy";


    public InvoiceEngineImpl(@Value("${qrcode.path}") String path, CompanyInfoService companyInfoService) {
        this.path = path;
        this.companyInfoService = companyInfoService;
    }


    /**
     * Creates invoice as pdf file based on entity object
     *
     * @param entity object which contains data for invoice
     * @throws FileNotFoundException when there is no found right existing path for saving
     *                               the invoice
     */
    @Override
    public String createInvoice(InvoiceSummaryEntity entity) throws IOException {


        var currentPlayer = entity.getPlayer();
        var currentUser = currentPlayer.getParent();
        var invoiceNumber = String.valueOf(entity.getGeneratedAt().getYear()) +
                entity.getGeneratedAt().getMonth().getValue() + currentPlayer.getId();
        var userEmail = currentUser.getEmail();
        var userTelephone = currentUser.getTelephoneNumber();
        var userName = currentUser.getFullName();
        var issuedDate = DateTimeFormatter.ofPattern(DATE_PATTERN).format(LocalDate.now());
        var dueDate = DateTimeFormatter.ofPattern(DATE_PATTERN).format(LocalDate.now().plusDays(14));
        var price = Double.valueOf(entity.getTotalCentsAmount() / 100.0);
        var monthString = entity.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault());
        var currency = entity.getCurrency();
        var identifier = UUID.randomUUID();
        var companyInfo = companyInfoService.getCompanyInfo();

        //Setting up for later initialization
        PdfWriter writer = new PdfWriter("pdf/invoice-" + identifier + ".pdf");
        PageSize ps = new PageSize(PageSize.A4);
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setDefaultPageSize(ps);
        PdfPage page = pdf.addNewPage();

        //Document initialization
        try (Document document = new Document(pdf)) {

            //Rectangle set. Sets up visible margin of document
            float margin = 30;
            Rectangle mediaBox = page.getMediaBox();
            Rectangle newMediaBox = new Rectangle(
                    mediaBox.getLeft() - margin,
                    mediaBox.getBottom() - margin,
                    mediaBox.getWidth() + margin * 2,
                    mediaBox.getHeight() + margin * 2);

            page.setMediaBox(newMediaBox);
            PdfCanvas over = new PdfCanvas(page);
            over.setStrokeColor(Color.BLACK);
            over.rectangle(mediaBox.getLeft(), mediaBox.getBottom(), mediaBox.getWidth(), mediaBox.getHeight());
            over.stroke();


            //Font setting
            PdfFont font = PdfFontFactory.createFont(FontConstants.COURIER, PdfEncodings.CP1250);
            document.setFont(font);

            //Adding title
            Paragraph title = new Paragraph("FAKTURA " + invoiceNumber)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(16)
                    .setBold();
            document.add(title.setMarginBottom(20));


            //Adding table fo header info
            float[] columnsWidths = new float[]{23f, 50f, 10f, 23f, 50f};
            Table table = new Table(UnitValue.createPercentArray(columnsWidths)).useAllAvailableWidth();

            addRowInHeader(
                    "Dodavatel",
                    companyInfo.companyName(),
                    "Odběratel",
                    userName,
                    table);
            addRowInHeader(
                    "Adresa",
                    companyInfo.address(),
                    null,
                    "Beskydská 12, Ostrava-Přívoz",
                    table);
            addRowInHeader(
                    "IČO",
                    companyInfo.businessId(),
                    null,
                    "987657897",
                    table);
            addRowInHeader(
                    "DIČ",
                    companyInfo.taxNumber(),
                    null,
                    null,
                    table);
            addRowInHeader(
                    "Email",
                    companyInfo.email(),
                    null,
                    userEmail,
                    table);
            addRowInHeader(
                    "Telefon",
                    companyInfo.telNumber(),
                    null,
                    userTelephone,
                    table);

            document.add(table);

            //Payment info
            Paragraph paymentInfoTitle = new Paragraph("Platební údaje").setBold().setMarginTop(50);
            document.add(paymentInfoTitle);

            Table paymentInfo = new Table(UnitValue.createPercentArray(new float[]{50f, 50f})).useAllAvailableWidth();
            addRowInPaymentInfo("Číslo účtu", companyInfo.bankAccount(), paymentInfo);
            addRowInPaymentInfo("Variabilní symbol", invoiceNumber, paymentInfo);
            addRowInPaymentInfo("Datum vystavení", issuedDate, paymentInfo);
            addRowInPaymentInfo("Datum zdanitelného plnění", issuedDate, paymentInfo);
            addRowInPaymentInfo("Datum splatnosti", dueDate, paymentInfo);

            document.add(paymentInfo);

            //Items
            Paragraph itemsTitle = new Paragraph("Předmět fakturace").setBold().setMarginTop(50);
            document.add(itemsTitle);

            Table items = new Table(UnitValue.createPercentArray(new float[]{80f, 20f})).useAllAvailableWidth();
            items.addHeaderCell(new Paragraph("Položka").setBold());
            items.addHeaderCell(new Paragraph("Cena").setBold());
            addRowInItems("Fakturace tréninků v měsíci " + monthString + " za hráče " +
                    currentPlayer.getFullName(), price, items);

            document.add(items);

            createQRCode(
                    price,
                    monthString,
                    invoiceNumber,
                    currency,
                    path,
                    identifier.toString(),
                    companyInfo.bankAccountInternationalFormat());

            addQrCode(document, path, identifier.toString());

        } catch (Exception e) {
            logErrorMessage(e.getMessage());
        }

        return "/files/invoice-" + identifier + ".pdf";

    }

    @Override
    public String createInvoice(PackageEntity entity) throws IOException {
        var players = entity.getPlayers();
        var playersNamesString = String.join(", ", players.stream().map(PlayerEntity::getFullName).toList());
        var identifier = UUID.randomUUID();
        var playersIds = players.stream().map(PlayerEntity::getId).map(String::valueOf).toList();
        var stringValueOfIds = String.join("", playersIds);
        var invoiceNumber = String.valueOf(entity.getGeneratedAt().getYear()) +
                entity.getGeneratedAt().getMonth().getValue() + stringValueOfIds;
        var companyInfo = companyInfoService.getCompanyInfo();
        var user = players.get(0).getParent();
        var userName = user.getFullName();
        var userEmail = user.getEmail();
        var userTelephone = user.getTelephoneNumber();
        var issuedDate = DateTimeFormatter.ofPattern(DATE_PATTERN).format(LocalDate.now());
        var dueDate = DateTimeFormatter.ofPattern(DATE_PATTERN).format(LocalDate.now().plusDays(14));
        var price = Double.valueOf(entity.getPricingRuleEntity().getAmountCents() / 100.0);
        var currency = entity.getPricingRuleEntity().getCurrency();


        //Setting up for later initialization
        PdfWriter writer = new PdfWriter("pdf/invoice-" + identifier + ".pdf");
        PageSize ps = new PageSize(PageSize.A4);
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setDefaultPageSize(ps);
        PdfPage page = pdf.addNewPage();

        //Document initialization
        try (Document document = new Document(pdf)) {

            //Rectangle set. Sets up visible margin of document
            float margin = 30;
            Rectangle mediaBox = page.getMediaBox();
            Rectangle newMediaBox = new Rectangle(
                    mediaBox.getLeft() - margin,
                    mediaBox.getBottom() - margin,
                    mediaBox.getWidth() + margin * 2,
                    mediaBox.getHeight() + margin * 2);

            page.setMediaBox(newMediaBox);
            PdfCanvas over = new PdfCanvas(page);
            over.setStrokeColor(Color.BLACK);
            over.rectangle(mediaBox.getLeft(), mediaBox.getBottom(), mediaBox.getWidth(), mediaBox.getHeight());
            over.stroke();


            //Font setting
            PdfFont font = PdfFontFactory.createFont(FontConstants.COURIER, PdfEncodings.CP1250);
            document.setFont(font);

            //Adding title
            Paragraph title = new Paragraph("FAKTURA " + invoiceNumber)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(16)
                    .setBold();
            document.add(title.setMarginBottom(20));


            //Adding table fo header info
            float[] columnsWidths = new float[]{23f, 50f, 10f, 23f, 50f};
            Table table = new Table(UnitValue.createPercentArray(columnsWidths)).useAllAvailableWidth();

            addRowInHeader(
                    "Dodavatel",
                    companyInfo.companyName(),
                    "Odběratel",
                    userName,
                    table);
            addRowInHeader(
                    "Adresa",
                    companyInfo.address(),
                    null,
                    "Beskydská 12, Ostrava-Přívoz",
                    table);
            addRowInHeader(
                    "IČO",
                    companyInfo.businessId(),
                    null,
                    "987657897",
                    table);
            addRowInHeader(
                    "DIČ",
                    companyInfo.taxNumber(),
                    null,
                    null,
                    table);
            addRowInHeader(
                    "Email",
                    companyInfo.email(),
                    null,
                    userEmail,
                    table);
            addRowInHeader(
                    "Telefon",
                    companyInfo.telNumber(),
                    null,
                    userTelephone,
                    table);

            document.add(table);

            //Payment info
            Paragraph paymentInfoTitle = new Paragraph("Platební údaje").setBold().setMarginTop(50);
            document.add(paymentInfoTitle);

            Table paymentInfo = new Table(UnitValue.createPercentArray(new float[]{50f, 50f})).useAllAvailableWidth();
            addRowInPaymentInfo("Číslo účtu", companyInfo.bankAccount(), paymentInfo);
            addRowInPaymentInfo("Variabilní symbol", invoiceNumber, paymentInfo);
            addRowInPaymentInfo("Datum vystavení", issuedDate, paymentInfo);
            addRowInPaymentInfo("Datum zdanitelného plnění", issuedDate, paymentInfo);
            addRowInPaymentInfo("Datum splatnosti", dueDate, paymentInfo);

            document.add(paymentInfo);

            //Items
            Paragraph itemsTitle = new Paragraph("Předmět fakturace").setBold().setMarginTop(50);
            document.add(itemsTitle);

            Table items = new Table(UnitValue.createPercentArray(new float[]{80f, 20f})).useAllAvailableWidth();
            items.addHeaderCell(new Paragraph("Položka").setBold());
            items.addHeaderCell(new Paragraph("Cena").setBold());
            addRowInItems("Fakturace předplatného tréninkového balíčku za hráče " + playersNamesString
                    , price, items);

            document.add(items);

            createQRCode(
                    price,
                    null,
                    invoiceNumber,
                    currency,
                    path,
                    identifier.toString(),
                    companyInfo.bankAccountInternationalFormat());

            addQrCode(document, path, identifier.toString());

        } catch (Exception e) {
            logErrorMessage(e.getMessage());
        }


        return "";
    }

    /**
     * Helper method. Adds row into main information area. Variables "firstSubject" and "firstValue"
     * are company info and variables secondSubject and secondValue are customer info.
     *
     * @param firstSubject  key to value of company information
     * @param firstValue    actual value of company information
     * @param secondSubject key to value of customer(user) information. Can be null
     *                      if is same as "firstSubject"
     * @param secondValue   actual value of customer information
     * @param table         Object where information is added
     */
    private void addRowInHeader(
            String firstSubject,
            String firstValue,
            String secondSubject,
            String secondValue,
            Table table) {

        if (secondSubject == null) {
            secondSubject = firstSubject;
        }
        if (secondValue == null) {
            secondValue = "";
        }
        table.addCell(new Cell().add(new Paragraph(firstSubject + ":").setBold()).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(firstValue)).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(secondSubject + ":").setBold()).setBorder(Border.NO_BORDER));
        table.addCell(new Cell().add(new Paragraph(secondValue)).setBorder(Border.NO_BORDER));
    }

    private void addRowInPaymentInfo(String subject, String value, Table table) {
        table.addCell(new Cell().add(new Paragraph(subject)));
        table.addCell(new Cell().add(new Paragraph(value)).setTextAlignment(TextAlignment.RIGHT));
    }

    private void addRowInItems(String item, Double price, Table table) {
        table.addCell(new Cell().add(new Paragraph(item)));
        table.addCell(new Cell().add(new Paragraph(
                String.format("%.2f", price).replace(",", ".") + ",- Kč")));
    }


    /**
     * Generates qr code and saves it to path in storage.
     *
     * @param price          actual price for services
     * @param month          month we make invoice for
     * @param variableSymbol identifier of payment
     * @param currency       currency in string format
     * @param path           actual place where the qr code is stored
     * @param identifier     Generated identifier for current qrCode
     * @throws WriterException when there is an error during making a qr code
     * @throws IOException     when there is an error during I/O operations
     */
    private void createQRCode(
            Double price,
            String month,
            String variableSymbol,
            String currency,
            String path,
            String identifier,
            String bankAccount) throws WriterException, IOException {

        bankAccount = bankAccount.replace(" ", "");
        String message;
        if (month == null) {
            message = "PLATBA ZA PACKAGE";
        } else {
            message = "PLATBA ZA MESIC " + month;
        }
        String finalPrice = String.format("%.2f", price).replace(",", ".");


        String spd = "SPD*1.0*ACC:" + bankAccount +
                "*AM:" + finalPrice +
                "*CC:" + currency.toUpperCase() +
                "*MSG:" + message.toUpperCase().trim() +
                "*X-VS:" + variableSymbol;


        BitMatrix matrix = new MultiFormatWriter().encode(spd, BarcodeFormat.QR_CODE, 300, 300);

        MatrixToImageWriter.writeToPath(
                matrix,
                "jpg",
                Paths.get(path + "/qrcode-" + identifier + ".jpg"));
    }

    /**
     * Adds qr code to the file(invoice in pdf format)
     *
     * @param document   current pdf document
     * @param path       path of where the qr code is stored
     * @param identifier generated unique identifier
     * @throws MalformedURLException when there is an error in given URL (path)
     */
    public static void addQrCode(
            Document document,
            String path,
            String identifier) throws MalformedURLException {

        Image image = new Image(ImageDataFactory.create(path + "/qrcode-" + identifier + ".jpg"));
        image.setHeight(200);
        image.setWidth(200);
        image.setMarginLeft(-25);

        document.add(new Paragraph("QR kód").setBold().setMarginTop(50));
        document.add(image);
    }

    public void logErrorMessage(String errorMessage) {
        log.error("Error while creating invoice: {} ", errorMessage);
    }


}
