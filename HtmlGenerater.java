package ex12;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.PlainDocument;

public class HtmlGenerater extends JFrame {
	JPopupMenu pmenu = new JPopupMenu();
	JTextArea ta;
	JPanel pane;

	public static void main(String[] args) {
		JFrame frame = new HtmlGenerater( "HtmlGenerater" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setSize( 500, 400 );
		frame.setVisible( true );
	}
	HtmlGenerater( String title ){
		super( title );
		pane = (JPanel)getContentPane();

		ta = new JTextArea();
		JScrollPane scr = new JScrollPane( ta );
		pane.add(scr, BorderLayout.CENTER);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar( menuBar );

		JMenu file = new JMenu( "ファイル" );
		menuBar.add( file );
		JMenu edit = new JMenu( "編集" );
		menuBar.add( edit );

		ta.addMouseListener( new CheckPopup() );

		Action newAction  = new NewAction("新規作成");

		Action loadAction = new LoadAction("開く");
		Action saveAction = new SaveAction("保存");
		Action exportAction = new ExportAction("出力");

		file.add( newAction );
		pmenu.add( newAction );

		file.addSeparator();
		pmenu.addSeparator();

		file.add( loadAction );
		pmenu.add( loadAction );

		file.add( saveAction );
		pmenu.add( saveAction );

		file.addSeparator();
		pmenu.addSeparator();

		file.add( exportAction );
		pmenu.add( exportAction );

		//------------------------------ 編集メニュー
		Action cutAction   = new CutAction( "切り取り" );
		Action copyAction  = new CopyAction( "コピー" );
		Action pasteAction = new PasteAction( "貼り付け" );

		edit.add( cutAction );
		edit.add( copyAction );
		edit.add( pasteAction );

		InputMap  inputMap  = pane.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW );
		ActionMap actionMap = pane.getActionMap();

		KeyStroke cutKey   = KeyStroke.getKeyStroke( "shift DELETE" );
		inputMap.put( cutKey, cutAction );
		actionMap.put( cutAction, cutAction );

		KeyStroke copyKey  = KeyStroke.getKeyStroke( "ctrl  INSERT" );
		inputMap.put( copyKey, copyAction );
		actionMap.put( copyAction, copyAction );

		KeyStroke pasteKey = KeyStroke.getKeyStroke( "shift INSERT" );
		inputMap.put( pasteKey, pasteAction );
		actionMap.put( pasteAction, pasteAction );


	}
	//--------------------------------- サブクラス
	class NewAction extends AbstractAction{
		NewAction( String text ){ super(text); }
		public void actionPerformed( ActionEvent e ){
			Object[] msg = {"全ての入力データが削除されます。\n本当に新規作成しますか？"};
			int ans = JOptionPane.showConfirmDialog(pane, msg, "確認",
					JOptionPane.YES_NO_OPTION);
			if(ans == JOptionPane.YES_OPTION ) {
				ta.setText( "" );
			}
		}
	}
	class LoadAction extends AbstractAction{
		LoadAction( String text ){ super(text); }
		@Override
		public void actionPerformed( ActionEvent e ){
			JFileChooser fileChooser = new JFileChooser( "." );
			int ret = fileChooser.showOpenDialog(pane);
			File file = fileChooser.getSelectedFile();      
			if( ret != JFileChooser.APPROVE_OPTION || file==null ) return; 
			try {
				FileReader reader = new FileReader( file );
				ta.read( reader, new PlainDocument() );
			}
			catch ( FileNotFoundException ex ) {
			}
			catch ( IOException ex ) {
			}
		}
	}
	class SaveAction extends AbstractAction{
		SaveAction( String text ){ super(text); }
		@Override
		public void actionPerformed( ActionEvent e ){
			JFileChooser fileChooser = new JFileChooser( "." );
			int ret = fileChooser.showSaveDialog(pane);
			String fileName = fileChooser.getSelectedFile().getAbsolutePath();
			if(fileName.contains(".") && !fileName.endsWith(".txt")) {
				Object[] msg = {"拡張子が異なっている可能性があります"};
				JOptionPane.showConfirmDialog(pane, msg, "確認",
						JOptionPane.DEFAULT_OPTION);
				return;
			}
			if(!fileName.endsWith(".txt")) {
				fileName += ".txt";
			}
			File file = new File(fileName);
			if( ret != JFileChooser.APPROVE_OPTION || file==null ) return; 
			try {
				FileWriter writer = new FileWriter( file );
				ta.write( writer );
				writer.close();
			}
			catch ( FileNotFoundException ex ) {
			}
			catch ( IOException ex ) {
			}
		}
	}

	class ExportAction extends AbstractAction{
		ExportAction( String text ){ super(text); }
		@Override
		public void actionPerformed( ActionEvent e ){
			JFileChooser fileChooser = new JFileChooser( "." );
			int ret = fileChooser.showSaveDialog(pane);
			String fileName = fileChooser.getSelectedFile().getAbsolutePath();
			if(fileName.contains(".") && !fileName.endsWith(".html")) {
				Object[] msg = {"拡張子が異なっている可能性があります"};
				JOptionPane.showConfirmDialog(pane, msg, "確認",
						JOptionPane.DEFAULT_OPTION);
				return;
			}
			if(!fileName.endsWith(".html")) {
				fileName += ".html";
			}
			File file = new File(fileName);
			if( ret != JFileChooser.APPROVE_OPTION || file==null ) return; 
			try {
				PrintWriter pWriter = new PrintWriter( file );
				pWriter.println("<html><meta charset=UTF-8\">");
				pWriter.println("<body>\n<p>");
				String[] text = ta.getText().split("\n");
				for(int i = 0 ; i < text.length ; i++) {
					pWriter.println(text[i] + "<br>\n");
				}
				pWriter.print("</p>\n</body>\n</html>");

				pWriter.close();
			}
			catch ( FileNotFoundException ex ) {
			}
		}
	}

	class CutAction extends AbstractAction{
		CutAction( String text ){ super(text); }
		@Override
		public void actionPerformed( ActionEvent e ){
			ta.cut();
		}
	}
	class CopyAction extends AbstractAction{
		CopyAction( String text ){ super(text); }
		@Override
		public void actionPerformed( ActionEvent e ){
			ta.copy();
		}
	}
	class PasteAction extends AbstractAction{
		PasteAction( String text ){ super(text); }
		@Override
		public void actionPerformed( ActionEvent e ){
			ta.paste();
		}
	}

	class CheckPopup extends MouseAdapter {
		@Override
		public void mousePressed( MouseEvent e ){
			if( SwingUtilities.isRightMouseButton( e ) ){
				pmenu.show( e.getComponent(), e.getX(), e.getY() );
			}
		}
	}
}