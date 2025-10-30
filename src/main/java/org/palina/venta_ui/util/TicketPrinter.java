package org.palina.venta_ui.util;

import org.palina.venta_ui.dto.DetalleVentaDto;
import org.palina.venta_ui.dto.PagoDto;
import org.palina.venta_ui.dto.ProductoDto;
import org.palina.venta_ui.dto.VentaDto;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.imageio.ImageIO;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

public class TicketPrinter {

    // Códigos ESC/POS
    private static final byte[] ALIGN_LEFT   = new byte[]{0x1B, 'a', 0};
    private static final byte[] ALIGN_CENTER = new byte[]{0x1B, 'a', 1};
    private static final byte[] ALIGN_RIGHT  = new byte[]{0x1B, 'a', 2};
    private static final byte[] BOLD_ON      = new byte[]{0x1B, 'E', 1};
    private static final byte[] BOLD_OFF     = new byte[]{0x1B, 'E', 0};
    private static final byte[] CUT_PAPER    = new byte[]{0x1D, 'V', 1};

    /**
     * Convierte una imagen PNG a bytes ESC/POS para impresión.
     */
    public static byte[] loadImageAsEscPos(String path) throws Exception {
        BufferedImage image = ImageIO.read(new File(path));

        int width = image.getWidth();
        int height = image.getHeight();
        int bytesPerRow = (width + 7) / 8;

        byte[] imageData = new byte[bytesPerRow * height];
        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x += 8) {
                byte b = 0;
                for (int bit = 0; bit < 8; bit++) {
                    int pixelX = x + bit;
                    if (pixelX < width) {
                        int color = image.getRGB(pixelX, y);
                        int r = (color >> 16) & 0xff;
                        int g = (color >> 8) & 0xff;
                        int bcol = color & 0xff;
                        int gray = (r + g + bcol) / 3;
                        if (gray < 128) { // Negro
                            b |= (1 << (7 - bit));
                        }
                    }
                }
                imageData[index++] = b;
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int y = 0; y < height; y++) {
            baos.write(0x1B);
            baos.write('*');
            baos.write(33); // modo 24-dot
            baos.write(bytesPerRow & 0xFF);
            baos.write((bytesPerRow >> 8) & 0xFF);

            baos.write(imageData, y * bytesPerRow, bytesPerRow);
            baos.write(0x0A);
        }

        return baos.toByteArray();
    }

    /**
     * Genera el ticket completo en formato ESC/POS
     *
     * @param venta objeto con datos de la venta
     * @param outletName nombre de la sucursal
     * @return byte[] listo para enviar a la impresora
     */
    public static void generarTicket(VentaDto venta, List<ProductoDto> productoDtoList,
                                     String outletName) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Logo
        //baos.write(ALIGN_CENTER);
        //baos.write(loadImageAsEscPos("/home/refrazul/pos/logo.png"));

        // Cabecera
        baos.write(ALIGN_CENTER);
        baos.write(BOLD_ON);
        baos.write("*** NOVEDADES ESTRELLITA ***\n".getBytes(StandardCharsets.ISO_8859_1));
        baos.write(BOLD_OFF);
        baos.write(("Sucursal " + outletName +  "\n").getBytes(StandardCharsets.ISO_8859_1));
        baos.write("--------------------------------\n".getBytes(StandardCharsets.ISO_8859_1));

        // Fecha y hora
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        baos.write(ALIGN_LEFT);
        baos.write(("Ticket: " + venta.getId() + "\n").getBytes(StandardCharsets.ISO_8859_1));
        baos.write(("Fecha/Hora: " + LocalDateTime.now().format(formatter) + "\n").getBytes(StandardCharsets.ISO_8859_1));

        // Cliente
        if (venta.getCustomer() != null && !venta.getCustomer().isEmpty()) {
            baos.write(("Cliente: " + venta.getCustomer() + "\n").getBytes(StandardCharsets.ISO_8859_1));
        }
        baos.write("--------------------------------\n".getBytes(StandardCharsets.ISO_8859_1));

        // Encabezado de tabla
        baos.write(String.format("%-15s %3s %8s\n", "Producto", "Cant", "Total").getBytes(StandardCharsets.ISO_8859_1));
        baos.write("--------------------------------\n".getBytes(StandardCharsets.ISO_8859_1));

        // Productos
        for (DetalleVentaDto d : venta.getSaleDetails()) {
            ProductoDto producto = productoDtoList.stream()
                    .filter(p -> p.getCode().equals(d.getProductCode()))
                    .findFirst()
                    .orElse(null);

            String nombre = producto != null ? producto.getCategory2() : d.getProductCode();
            if (nombre.length() > 15) nombre = nombre.substring(0, 15);
            BigDecimal subtotal = d.getSubtotal();
            baos.write(String.format("%-15s %3d %8.2f\n", nombre, d.getQuantity(), subtotal).getBytes(StandardCharsets.ISO_8859_1));
        }

        baos.write("--------------------------------\n".getBytes(StandardCharsets.ISO_8859_1));

        // Total
        baos.write(ALIGN_RIGHT);
        baos.write(String.format("TOTAL: %.2f\n", venta.getTotal()).getBytes(StandardCharsets.ISO_8859_1));

        String tipoPago = null;

        if(null != venta.getPaymentType() && !venta.getPayments().isEmpty() ){
            tipoPago = venta.getPayments().get(0).getPaymentType();
        }

        // Tipo de venta y pago
        baos.write(ALIGN_LEFT);
        if(!venta.getSaleType().equalsIgnoreCase("Contado")){
            baos.write(("Tipo de venta: " + venta.getSaleType() + "\n").getBytes(StandardCharsets.ISO_8859_1));
        }

        if(null != tipoPago){
            baos.write(("Tipo de pago: " + venta.getPaymentType() + "\n").getBytes(StandardCharsets.ISO_8859_1));
        }


        // Pagos detallados
        /*if (venta.getPayments() != null && !venta.getPayments().isEmpty()) {
            baos.write("--------------------------------\n".getBytes(StandardCharsets.ISO_8859_1));
            baos.write("Pagos:\n".getBytes(StandardCharsets.ISO_8859_1));
            for (PagoDto pago : venta.getPayments()) {
                String importe = String.valueOf(pago.getAmountPaid());
                //baos.write(String.format(" - %-12s %8.2f\n", pago.getPaymentType(), importe).getBytes(StandardCharsets.ISO_8859_1));
            }
        }*/

        // Pie
        baos.write("\n".getBytes(StandardCharsets.ISO_8859_1));
        baos.write(ALIGN_CENTER);
        baos.write("¡Gracias por su compra!\n".getBytes(StandardCharsets.ISO_8859_1));
        baos.write("   Vuelva pronto :)\n".getBytes(StandardCharsets.ISO_8859_1));

        // Saltos y corte
        baos.write("\n\n\n".getBytes(StandardCharsets.ISO_8859_1));
        baos.write(CUT_PAPER);

        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        if (services.length == 0) {
            System.err.println("❌ No se encontraron impresoras disponibles.");
            return;
        }

        // 2️⃣ Buscar impresora RAW/TERMAL
        PrintService impresora = null;
        for (PrintService ps : services) {
            String nombre = ps.getName().toLowerCase();
            if (nombre.contains("ticket") || nombre.contains("raw")) {
                impresora = ps;
                break;
            }
        }

        // Si no se encontró, usar la primera
        if (impresora == null) {
            impresora = services[0];
        }

        System.out.println("🖨️ Imprimiendo en: " + impresora.getName());

        // 3️⃣ Preparar el documento como bytes
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        Doc doc = new SimpleDoc(baos.toByteArray(), flavor, null);

        // 4️⃣ Crear el trabajo de impresión
        DocPrintJob job = impresora.createPrintJob();
        PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();

        job.print(doc, attrs);
        System.out.println("✅ Impresión completada.");
    }
}