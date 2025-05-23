package pages;

import components.NavigationBar;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.ImageWriteParam;
import javax.imageio.IIOImage;

public class AIAssistantPage extends BasePage {
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color DESCRIPTION_COLOR = new Color(120, 120, 120);
    private static final Color USER_BUBBLE_COLOR = new Color(0, 120, 215);
    private static final Color AI_BUBBLE_COLOR = new Color(240, 240, 240);
    private static final int WIDTH = 500;
    private static final int HEIGHT = 800;

    private final QianfanClient aiClient;
    private JPanel chatPanel;
    private JScrollPane chatScrollPane;
    private JTextField messageField;
    private File lastSelectedImage = null; // 保存用户选择的图片
    private boolean hasJumpedToAccountingPage = false; // 标记是否已跳转过记账页面

    public AIAssistantPage() {
        super();
        setTitle("AI 助手");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.aiClient = new QianfanClient("bce-v3/ALTAK-HJ3TpjzoWzueUoE8DOZam/32d4bed1788f1615fe1b9e62fe85dbc21fb16f79");
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                // 只有没有通过一键记账跳转时，才查找并显示记账页面
                if (!hasJumpedToAccountingPage) {
                    for (Frame frame : Frame.getFrames()) {
                        if (frame instanceof AccountingPage) {
                            frame.setVisible(true);
                            frame.toFront();
                            return;
                        }
                    }
                    // 如果没有找到 AccountingPage，这里什么都不做
                }
            }
        });
        createComponents();
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        // 添加一键记账按钮
        JButton quickAccountButton = new JButton("一键记账");
        quickAccountButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        quickAccountButton.setForeground(Color.WHITE);
        quickAccountButton.setBackground(USER_BUBBLE_COLOR);
        quickAccountButton.setBorderPainted(false);
        quickAccountButton.setFocusPainted(false);
        quickAccountButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        quickAccountButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        quickAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("图片文件", "jpg", "jpeg", "png", "gif");
                fileChooser.setFileFilter(filter);

                int result = fileChooser.showOpenDialog(AIAssistantPage.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedImage = fileChooser.getSelectedFile();
                    addUserImageMessage(selectedImage);
                    
                    new Thread(() -> {
                        try {
                            String base64Image = encodeImageToBase64(selectedImage);
                            String prompt = "请分析这张图片中的消费小票或账单，提取以下信息：\n" +
                                          "1. 消费金额\n" +
                                          "2. 消费类别（如餐饮、交通、购物等）\n" +
                                          "3. 商家名称（如果有）\n" +
                                          "请以JSON格式返回，格式如下：\n" +
                                          "{\"amount\": 金额, \"category\": \"类别\", \"merchant\": \"商家名称\"}";
                            
                            String response = aiClient.chatWithImage("ernie-4.5-8k-preview", base64Image);
                            SwingUtilities.invokeLater(() -> {
                                addAIMessage(response);
                                // 解析AI返回的JSON数据
                                try {
                                    // 提取JSON部分
                                    String jsonStr = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
                                    JSONObject json = new JSONObject(jsonStr);
                                    
                                    // 获取解析出的数据
                                    double amount = json.optDouble("amount", 0.0);
                                    String category = json.optString("category", "OTHER");
                                    String merchant = json.optString("merchant", "");
                                    
                                    // 查找是否已存在 AccountingPage 实例
                                    AccountingPage accountingPage = null;
                                    for (Frame frame : Frame.getFrames()) {
                                        if (frame instanceof AccountingPage) {
                                            accountingPage = (AccountingPage) frame;
                                            break; // 找到已有的记账页面
                                        }
                                    }

                                    if (accountingPage != null) {
                                        // 更新数据并显示页面
                                        accountingPage.setPreFilledData(amount, category, merchant);
                                        accountingPage.setVisible(true);
                                        accountingPage.toFront();
                                        hasJumpedToAccountingPage = true;
                                    } else {
                                        // 如果没有找到 AccountingPage 实例，创建一个新的并填充数据
                                        // 注意：根据应用流程，理论上从AccountingPage打开AI Assistant，所以总会找到一个实例。
                                        // 但作为回退机制，这里保留创建新页面的逻辑。
                                        AccountingPage newAccountingPage = new AccountingPage();
                                        newAccountingPage.setPreFilledData(amount, category, merchant);
                                        newAccountingPage.setVisible(true);
                                        hasJumpedToAccountingPage = true;
                                    }
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(AIAssistantPage.this,
                                            "解析账单信息失败，请手动记账",
                                            "提示",
                                            JOptionPane.WARNING_MESSAGE);
                                } finally {
                                     // 确保处理完成后关闭 AI 助手页面
                                     dispose();
                                }
                            });
                        } catch (Exception ex) {
                            SwingUtilities.invokeLater(() -> {
                                addAIMessage("抱歉，处理图片时发生错误: " + ex.getMessage());
                                // 确保处理完成后关闭 AI 助手页面
                                dispose();
                            });
                        }
                    }).start();
                }
            }
        });

        // 创建顶部面板
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(quickAccountButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 创建聊天面板
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(BACKGROUND_COLOR);
        chatPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 添加欢迎消息
        addAIMessage("你好！我是你的AI助手，支持图文提问，请发送图片或文字吧～");

        chatScrollPane = new JScrollPane(chatPanel);
        chatScrollPane.setBorder(null);
        chatScrollPane.setBackground(BACKGROUND_COLOR);
        chatScrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        mainPanel.add(chatScrollPane, BorderLayout.CENTER);

        // 创建底部容器面板
        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBackground(BACKGROUND_COLOR);

        // 消息输入区域
        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        messagePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        messageField = new JTextField("请输入您的问题...");
        messageField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        messageField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        messageField.setForeground(Color.GRAY);

        messageField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (messageField.getText().equals("请输入您的问题...")) {
                    messageField.setText("");
                    messageField.setForeground(TEXT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (messageField.getText().isEmpty()) {
                    messageField.setText("请输入您的问题...");
                    messageField.setForeground(Color.GRAY);
                }
            }
        });

        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // 发送按钮
        JButton sendButton = new JButton("发送");
        sendButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        sendButton.setForeground(Color.WHITE);
        sendButton.setBackground(USER_BUBBLE_COLOR);
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                // 处理文字和图片混合发送
                if (!message.isEmpty() && !message.equals("请输入您的问题...") || lastSelectedImage != null) {
                    if (!message.isEmpty() && !message.equals("请输入您的问题...")) {
                        addUserMessage(message);
                    }

                    if (lastSelectedImage != null) {
                        addUserImageMessage(lastSelectedImage);
                    }

                    messageField.setText("");

                    new Thread(() -> {
                        try {
                            String response;
                            if (lastSelectedImage != null) {
                                // 只发送图片请求
                                String base64Image = encodeImageToBase64(lastSelectedImage);
                                response = aiClient.chatWithImage("ernie-4.5-8k-preview", base64Image);
                                lastSelectedImage = null; // 发送后清空
                            } else {
                                // 只发送文字请求
                                response = aiClient.chat("ernie-4.5-8k-preview", message);
                            }
                            SwingUtilities.invokeLater(() -> {
                                addAIMessage(response);
                            });
                        } catch (Exception ex) {
                            SwingUtilities.invokeLater(() -> {
                                addAIMessage("抱歉，发生了错误: " + ex.getMessage());
                            });
                        }
                    }).start();
                }
            }
        });

        // 上传按钮
        JButton uploadButton = new JButton("📸");
        uploadButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        uploadButton.setForeground(TEXT_COLOR);
        uploadButton.setBackground(Color.WHITE);
        uploadButton.setBorderPainted(false);
        uploadButton.setContentAreaFilled(false);
        uploadButton.setFocusPainted(false);
        uploadButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("图片文件", "jpg", "jpeg", "png", "gif");
                fileChooser.setFileFilter(filter);

                int result = fileChooser.showOpenDialog(AIAssistantPage.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    lastSelectedImage = fileChooser.getSelectedFile();
                    // 预览图片，不立即发送
                    JOptionPane.showMessageDialog(AIAssistantPage.this,
                            "已选择图片: " + lastSelectedImage.getName() + "\n请输入问题描述后点击发送");
                }
            }
        });

        buttonPanel.add(sendButton);
        buttonPanel.add(uploadButton);

        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(buttonPanel, BorderLayout.EAST);

        JPanel messageWrapper = new JPanel(new BorderLayout());
        messageWrapper.setBackground(BACKGROUND_COLOR);
        messageWrapper.add(messagePanel, BorderLayout.CENTER);
        messageWrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        bottomContainer.add(messageWrapper, BorderLayout.CENTER);

        mainPanel.add(bottomContainer, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addUserMessage(String message) {
        JPanel messagePanel = createMessageBubble(message, true);
        chatPanel.add(messagePanel);
        chatPanel.add(Box.createVerticalStrut(10));
        chatPanel.revalidate();
        chatPanel.repaint();
        scrollToBottom();
    }

    private void addAIMessage(String message) {
        JPanel messagePanel = createMessageBubble(message, false);
        chatPanel.add(messagePanel);
        chatPanel.add(Box.createVerticalStrut(10));
        chatPanel.revalidate();
        chatPanel.repaint();
        scrollToBottom();
    }

    private void addUserImageMessage(File imageFile) {
        JPanel messagePanel = createImageBubble(imageFile, true);
        chatPanel.add(messagePanel);
        chatPanel.add(Box.createVerticalStrut(10));
        chatPanel.revalidate();
        chatPanel.repaint();
        scrollToBottom();
    }

    private JPanel createMessageBubble(String message, boolean isUser) {
        JPanel bubblePanel = new JPanel();
        bubblePanel.setLayout(new BoxLayout(bubblePanel, BoxLayout.X_AXIS));
        bubblePanel.setBackground(BACKGROUND_COLOR);
        bubblePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel avatarLabel = new JLabel(isUser ? "👤" : "🤖");
        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        avatarLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        JPanel messageBubble = new JPanel();
        messageBubble.setLayout(new BoxLayout(messageBubble, BoxLayout.Y_AXIS));
        messageBubble.setBackground(isUser ? USER_BUBBLE_COLOR : AI_BUBBLE_COLOR);
        messageBubble.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel messageLabel = new JLabel("<html><body style='width: 300px'>" + message + "</body></html>");
        messageLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        messageLabel.setForeground(isUser ? Color.WHITE : TEXT_COLOR);

        messageBubble.add(messageLabel);

        if (isUser) {
            bubblePanel.add(Box.createHorizontalGlue());
            bubblePanel.add(messageBubble);
            bubblePanel.add(avatarLabel);
        } else {
            bubblePanel.add(avatarLabel);
            bubblePanel.add(messageBubble);
            bubblePanel.add(Box.createHorizontalGlue());
        }

        return bubblePanel;
    }

    private JPanel createImageBubble(File imageFile, boolean isUser) {
        JPanel bubblePanel = new JPanel();
        bubblePanel.setLayout(new BoxLayout(bubblePanel, BoxLayout.X_AXIS));
        bubblePanel.setBackground(BACKGROUND_COLOR);
        bubblePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel avatarLabel = new JLabel(isUser ? "👤" : "🤖");
        avatarLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        avatarLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        JPanel messageBubble = new JPanel();
        messageBubble.setLayout(new BorderLayout());
        messageBubble.setBackground(isUser ? USER_BUBBLE_COLOR : AI_BUBBLE_COLOR);
        messageBubble.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        try {
            BufferedImage img = ImageIO.read(imageFile);
            int width = img.getWidth();
            int height = img.getHeight();

            // 设置最大显示尺寸，但保持原始比例
            int maxWidth = 300;
            int maxHeight = 300;

            double ratio = Math.min((double)maxWidth / width, (double)maxHeight / height);
            int scaledWidth = (int) (width * ratio);
            int scaledHeight = (int) (height * ratio);

            ImageIcon icon = new ImageIcon(img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH));
            JLabel imageLabel = new JLabel(icon);
            messageBubble.add(imageLabel, BorderLayout.CENTER);
        } catch (Exception ex) {
            JLabel errorLabel = new JLabel("[图片加载失败]");
            messageBubble.add(errorLabel, BorderLayout.CENTER);
        }

        if (isUser) {
            bubblePanel.add(Box.createHorizontalGlue());
            bubblePanel.add(messageBubble);
            bubblePanel.add(avatarLabel);
        } else {
            bubblePanel.add(avatarLabel);
            bubblePanel.add(messageBubble);
            bubblePanel.add(Box.createHorizontalGlue());
        }
        return bubblePanel;
    }
    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = chatScrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private void handleNavigation(ActionEvent e) {
        String command = e.getActionCommand();
        dispose();
        switch (command) {
            case "DATE":
                new CalendarPage().setVisible(true);
                break;
            case "ADD":
                new AccountingPage().setVisible(true);
                break;
            case "MINE":
                new MinePage().setVisible(true);
                break;
            case "HOME":
                new MainPage().setVisible(true);
                break;
        }
    }

    // 将图片转换为Base64编码
    private String encodeImageToBase64(File imageFile) throws IOException {
        BufferedImage originalImage = ImageIO.read(imageFile);
        
        // 计算压缩后的尺寸
        int maxDimension = 800; // 最大边长
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        
        int newWidth = originalWidth;
        int newHeight = originalHeight;
        
        if (originalWidth > maxDimension || originalHeight > maxDimension) {
            if (originalWidth > originalHeight) {
                newWidth = maxDimension;
                newHeight = (int) ((double) originalHeight / originalWidth * maxDimension);
            } else {
                newHeight = maxDimension;
                newWidth = (int) ((double) originalWidth / originalHeight * maxDimension);
            }
        }
        
        // 创建压缩后的图片
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();
        
        // 转换为JPEG格式并压缩
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(0.7f); // 压缩质量
        
        ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
        writer.setOutput(ios);
        writer.write(null, new IIOImage(resizedImage, null, null), param);
        writer.dispose();
        ios.close();
        
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    // QianfanClient内部类 - 增强支持图片分析
    private class QianfanClient {
        private final String apiKey;
        private final HttpClient client;
        private static final String BASE_URL = "https://qianfan.baidubce.com/v2/chat/completions";

        // 显式初始化ArrayList
        private final List<Map<String, Object>> messages;

        public QianfanClient(String apiKey) {
            this.apiKey = apiKey;
            this.client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            // 在构造函数中初始化
            this.messages = new ArrayList<>();

            // 设置系统提示
            messages.add(createMessage("system", "你是一个专业的智能记账助手。你的主要职责是：\n" +
                    "1. 分析用户上传的消费小票、账单等图片，提取关键信息\n" +
                    "2. 识别消费金额、消费类别、商家名称等信息\n" +
                    "3. 将信息以下面的格式返回，格式为：您本次的账单为：{\"amount\": 金额, \"category\": \"类别\", \"merchant\": \"商家名称\"}\n" +
                    "4. 对于无法识别的信息，使用null值表示\n" +
                    "5. 消费类别应归类为：FOOD、FRUIT、LOVE、TRIP、EDU、CURE、GOTO、MAKEUP、SWEET、OTHER等类别\n" +
                    "6. 金额应提取为数字，不包含货币符号\n" +
                    "7. 如果图片中信息不完整，只返回能识别的信息"));
        }

        // 创建文字消息
        private Map<String, Object> createMessage(String role, String content) {
            Map<String, Object> message = new HashMap<>();
            message.put("role", role);
            message.put("content", content);
            return message;
        }

        // 创建图片消息
        private Map<String, Object> createImageMessage(String role, String base64Image) {
            Map<String, Object> message = new HashMap<>();
            message.put("role", role);

            // 构建图片内容
            JSONObject content = new JSONObject();
            content.put("type", "image_url");
            HashMap<String, Object> objectObjectHashMap = new HashMap<String, Object>();
            objectObjectHashMap.put("url", "data:image/jpeg;base64," + base64Image);
            content.put("image_url", objectObjectHashMap);
            ArrayList<Object> objects = new ArrayList<>();
            objects.add(content);
            message.put("content", objects);
            return message;
        }

        // 处理纯文本请求
        public String chat(String model, String userInput) throws IOException, InterruptedException {
            messages.add(createMessage("user", userInput));
            return sendRequest(model);
        }

        // 处理图片请求
        public String chatWithImage(String model, String base64Image) throws IOException, InterruptedException {
            messages.add(createImageMessage("user", base64Image));
            return sendRequest(model);
        }

        // 发送请求的通用方法
        private String sendRequest(String model) throws IOException, InterruptedException {
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);
            requestBody.put("messages", new JSONArray(messages));

            String jsonBody = requestBody.toString();
            System.out.println("Request body: " + jsonBody); // 添加调试输出

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Response status: " + response.statusCode()); // 添加调试输出
            System.out.println("Response body: " + response.body()); // 添加调试输出

            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                String aiReply = jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
                messages.add(createMessage("assistant", aiReply));
                return aiReply;
            } else {
                throw new IOException("请求失败: " + response.statusCode() + ", " + response.body());
            }
        }
    }
} 