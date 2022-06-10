package version2;

import java.awt.Color;
import java.awt.Container;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

/**
 * 登录窗口 模态对话框
 * 
 * @author RoomLess
 *
 */
public class LoginDialog extends JDialog implements ActionListener {
	private JPanel panel = new JPanel();
	private JLabel lblAccount = new JLabel("账户:");
	private JTextField txtAccount = new JTextField();
	private JLabel lblPassword = new JLabel("密码:");
	private TextField txtPassword = new TextField();
	private JButton btnLogin = new JButton("登录");
	private JButton btnCancel = new JButton("取消");

	public LoginDialog() {

		Container contentPane = this.getContentPane();
		contentPane.setLayout(null);

		panel.setLayout(null);
		panel.setBounds(72, 31, 304, 123);
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPane.add(panel);

		lblAccount.setBounds(24, 31, 54, 15);
		panel.add(lblAccount);

		txtAccount.setBounds(73, 28, 196, 21);
		txtAccount.setColumns(10);
		panel.add(txtAccount);

		lblPassword.setBounds(24, 75, 42, 15);
		panel.add(lblPassword);

		txtPassword.setBounds(73, 72, 196, 21);
		txtPassword.setColumns(10);
		txtPassword.setEchoChar('*');
		panel.add(txtPassword);

		btnLogin.setBounds(72, 164, 93, 23);
		contentPane.add(btnLogin);
		btnLogin.addActionListener(this);

		btnCancel.setBounds(283, 164, 93, 23);
		contentPane.add(btnCancel);
		btnCancel.addActionListener(this);

		this.setLocationRelativeTo(null);

		this.setSize(450, 260);
		this.setResizable(false);
		this.setTitle("登录");
	}

	public static void main(String[] args) {
		LoginDialog dlg = new LoginDialog();
		dlg.setModal(true);
		dlg.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO 自动生成的方法存根
		if (arg0.getSource() == this.btnCancel) {
			this.dispose();
		} else if (arg0.getSource() == this.btnLogin) {
			// 检查用户输入的账户是否有效
			if (this.txtAccount.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "账户不能为空");
				return;
			}
			// 检查用户输入的密码是否有效
			if (this.txtPassword.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "密码不能为空");
				return;
			}

		}

		try {
			// 在上下文中记录用户输入的账户
			Context.account = this.txtAccount.getText();
			// 连接服务器软件
			Context.socket = new Socket("192.168.43.170", 8086);
										//192.168.43.170
										//180.76.236.34
			// 准备发送对象
			Context.writer = new BufferedWriter(new OutputStreamWriter(Context.socket.getOutputStream()));
			// 发送登录请求信息
			Context.writer.write("login;" + this.txtAccount.getText() + ";" + this.txtPassword.getText() + "\n");
			Context.writer.flush();
			// 接受服务器的登录结果反馈
			Context.reader = new BufferedReader(new InputStreamReader(Context.socket.getInputStream()));
			String line = Context.reader.readLine();
			// 将接受到的反馈字符串按照分号分解开
			String[] s = line.split(";");
			// 如果反馈字符串开始不是“loginresult”,说明服务器反馈有误
			if (!s[0].equalsIgnoreCase("loginresult")) {
				JOptionPane.showMessageDialog(this, "登陆失败:返回信息无效");
			}
			// 反馈字符串开始是“loginresult”
			else {
				// 反馈字符串第二部分是ok，说明登录验证成功
				if (s[1].equalsIgnoreCase("OK")) {
					JOptionPane.showMessageDialog(this, "登陆成功");
					ReceiveThread recvThread = new ReceiveThread(Context.socket);
					recvThread.start();
					this.dispose();
					return;
				} else {// 否则说明登录失败
					JOptionPane.showMessageDialog(this, "登陆失败,原因:" + s[2]);
					return;
				}
			}

		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "登陆失败:" + e.getMessage());
			return;
		}
	}
}
