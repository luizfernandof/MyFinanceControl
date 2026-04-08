package br.com.devl.mfc.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import br.com.devl.mfc.auth.entity.User;
import br.com.devl.mfc.entity.Transaction;
import br.com.devl.mfc.enums.TransactionType;
import br.com.devl.mfc.repository.TransactionRepository;

@Service
public class ReportService {

	private final TransactionRepository transactionRepository;

	private static final DeviceRgb GREEN = new DeviceRgb(34, 139, 34);
	private static final DeviceRgb RED = new DeviceRgb(220, 53, 69);
	private static final Locale BRAZIL = new Locale("pt", "BR");
	private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(BRAZIL);

	public ReportService(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	public byte[] generateMonthlyTransactionsPdf(User user, int month, int year) {
		List<Transaction> transactions = transactionRepository.findByUserAndMonthAndYearOrderByDate(user, month,
				year);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PdfWriter writer = new PdfWriter(outputStream);
		PdfDocument pdf = new PdfDocument(writer);
		Document document = new Document(pdf);

		String monthName = LocalDate.of(year, month, 1).getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
		String title = "Relatório de Transações - " + monthName + " de " + year;

		addTitle(document, title);

		if (transactions.isEmpty()) {
			addEmptyMessage(document);
		} else {
			addSummary(document, transactions, month, year);
			addTransactionsTable(document, transactions);
		}

		addFooter(document);

		document.close();
		return outputStream.toByteArray();
	}

	private void addTitle(Document document, String title) {
		Paragraph titleParagraph = new Paragraph(title).setFontSize(20).setBold()
				.setTextAlignment(TextAlignment.CENTER).setMarginBottom(20);
		document.add(titleParagraph);
	}

	private void addEmptyMessage(Document document) {
		Paragraph emptyParagraph = new Paragraph("Nenhuma transação encontrada para o período especificado.")
				.setFontSize(12).setTextAlignment(TextAlignment.CENTER).setMarginTop(30);
		document.add(emptyParagraph);
	}

	private void addSummary(Document document, List<Transaction> transactions, int month, int year) {
		BigDecimal totalIncome = BigDecimal.ZERO;
		BigDecimal totalExpense = BigDecimal.ZERO;

		for (Transaction t : transactions) {
			if (t.getType() == TransactionType.INCOME) {
				totalIncome = totalIncome.add(t.getAmount());
			} else {
				totalExpense = totalExpense.add(t.getAmount());
			}
		}

		BigDecimal balance = totalIncome.subtract(totalExpense);

		Table summaryTable = new Table(UnitValue.createPercentArray(new float[] { 1, 1, 1 })).useAllAvailableWidth()
				.setMarginBottom(20);

		addSummaryCell(summaryTable, "Total Receitas", totalIncome, GREEN);
		addSummaryCell(summaryTable, "Total Despesas", totalExpense, RED);
		addSummaryCell(summaryTable, "Saldo", balance, balance.compareTo(BigDecimal.ZERO) >= 0 ? GREEN : RED);

		document.add(summaryTable);
	}

	private void addSummaryCell(Table table, String label, BigDecimal value, DeviceRgb color) {
		Cell cell = new Cell().setBorder(Border.NO_BORDER).setPadding(10);
		cell.add(new Paragraph(label).setFontSize(10).setBold());
		cell.add(new Paragraph(CURRENCY_FORMAT.format(value)).setFontSize(14).setFontColor(color));
		table.addCell(cell);
	}

	private void addTransactionsTable(Document document, List<Transaction> transactions) {
		float[] columnWidths = { 2, 4, 3, 2 };
		Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

		addTableHeader(table, "Data");
		addTableHeader(table, "Descrição");
		addTableHeader(table, "Categoria");
		addTableHeader(table, "Valor");

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		for (Transaction t : transactions) {
			boolean isIncome = t.getType() == TransactionType.INCOME;

			addTableCell(table, t.getDate().format(dateFormatter), TextAlignment.LEFT);
			addTableCell(table, t.getDescription(), TextAlignment.LEFT);
			addTableCell(table, t.getCategory().getName(), TextAlignment.LEFT);

			Paragraph valueParagraph = new Paragraph((isIncome ? "+ " : "- ") + CURRENCY_FORMAT.format(t.getAmount()))
					.setFontColor(isIncome ? GREEN : RED).setTextAlignment(TextAlignment.RIGHT);
			Cell valueCell = new Cell().setTextAlignment(TextAlignment.RIGHT);
			valueCell.add(valueParagraph);
			table.addCell(valueCell);
		}

		document.add(table);
	}

	private void addTableHeader(Table table, String header) {
		Cell cell = new Cell().setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold().setPadding(8)
				.setTextAlignment(TextAlignment.CENTER);
		cell.add(new Paragraph(header));
		table.addHeaderCell(cell);
	}

	private void addTableCell(Table table, String content, TextAlignment alignment) {
		Cell cell = new Cell().setPadding(6).setTextAlignment(alignment);
		cell.add(new Paragraph(content));
		table.addCell(cell);
	}

	private void addFooter(Document document) {
		Paragraph footer = new Paragraph("Gerado em " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
				.setFontSize(8).setTextAlignment(TextAlignment.RIGHT).setMarginTop(30);
		document.add(footer);
	}
}
