package version2;

import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ActionEvent;

class Desktop{
	private JButton btnDesktopImage;
	public JTextField txtDesktopPlayer1;
	public JTextField txtDesktopPlayer2;
	public JButton btnDesktopJoin;
	public Desktop(JDialog parent,int topx,int topy) {
		Container contentPane =parent.getContentPane();
		btnDesktopImage = new JButton("");
		btnDesktopImage
				.setIcon(new ImageIcon("D:\\eclipse-workspace\\GoBangClient\\src\\version2\\棋盘区.png"));
		btnDesktopImage.setBounds(topx, topy, 132, 126);
		contentPane.add(btnDesktopImage);

		txtDesktopPlayer1 = new JTextField();
		txtDesktopPlayer1.setText("玩家一");
		txtDesktopPlayer1.setBounds(topx, topy+136, 132, 21);
		contentPane.add(txtDesktopPlayer1);
		txtDesktopPlayer1.setColumns(10);

		txtDesktopPlayer2 = new JTextField();
		txtDesktopPlayer2.setText("玩家二");
		txtDesktopPlayer2.setColumns(10);
		txtDesktopPlayer2.setBounds(topx, topy+166, 132, 21);
		contentPane.add(txtDesktopPlayer2);

		btnDesktopJoin = new JButton("加入");
		btnDesktopJoin.setBounds(topx+17, topy+206, 97, 23);
		btnDesktopJoin.addActionListener((ActionListener) parent);
		contentPane.add(btnDesktopJoin);

	}
}

public class GameLoggyDialog extends JDialog implements ActionListener, WindowListener {

	public Desktop[] desktops =new Desktop[6];

	public GameLoggyDialog() {
		// 设置内容面板的缺省布局无效
		Container contentPane = this.getContentPane();
		contentPane.setLayout(null);

		// 第一个棋桌
		desktops[0] =new Desktop(this,23,10);
		desktops[1] =new Desktop(this,254,10);
		desktops[2] =new Desktop(this,23,249);
		desktops[3] =new Desktop(this,254,249);
		desktops[4] =new Desktop(this,481,10);
		desktops[5] =new Desktop(this,481,249);
		//让自己监听窗口发生的事件
		this.addWindowListener(this);
		// 设置窗口居中
		this.setLocationRelativeTo(null);
		// 窗口关闭方式
		// this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// 窗口大小
		this.setSize(696, 522);
		// 窗口大小固定
		this.setResizable(false);
		// 窗口标题
		this.setTitle("游戏大厅");

		// 向服务器发送棋盘更新提醒
		Context.send("desktop;list;1");
		Context.send("desktop;list;2");
		Context.send("desktop;list;3");
		Context.send("desktop;list;4");
		Context.send("desktop;list;5");
		Context.send("desktop;list;6");

	}

	/**
	 * 更新某个棋桌的状态
	 * 
	 * @param desktopno 期盘编号
	 * @param players   该棋桌现有玩家名
	 */
	public void updateDesktop(int desktopno, String[] players) {
		Desktop d=this.desktops[desktopno -1];
		
		
			d.txtDesktopPlayer1.setText(players != null && players.length >= 1 && players[0] != null ? players[0] : "");
			d.txtDesktopPlayer2.setText(players != null && players.length >= 2 && players[1] != null ? players[0] : "");
			this.repaint();

	}

//如何不用if,减轻代码数量
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO 自动生成的方法存根
		JButton clickedButton =(JButton) arg0.getSource();
		for(int i =0 ;i<this.desktops.length;i++) {
			Desktop d= this.desktops[i];
			if(d.btnDesktopJoin  ==clickedButton) {
			Context.send("desktop;join;"+(i+1));	
			}
		}
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO 自动生成的方法存根
		Context.loggyDialog = null;
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO 自动生成的方法存根

	}
}
