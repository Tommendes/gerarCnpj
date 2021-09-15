/*
 * Decompiled with CFR 0_115.
 */
package gerarcnpj;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoMy {

    // O endereço da base de dados
    private static final String USER = "tommen_sistemas";
    private static final String PASSWORD = "Zuo7G54Fm4btIRvQkQ";
    private static final String BD = "tommen_cadastros";
    private static final String URL = "jdbc:mysql://ns1.tommendes.com.br/" + BD + "?user=" + USER + "&password=" + PASSWORD;
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * Conectar ao BD
     */
    public void conectar() {
        try {
            mensagem("Conectar ao BD");
//            mensagem("Conectar a: " + URL);
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            this.setConnection(conn);
        } catch (SQLException e) {
            mensagem("Erro: " + e.getMessage());
        } finally {
            mensagem("Conectado");
        }
    }

    /**
     * Desconectar ao BD
     */
    public void desconectar() {
        try {
            mensagem("Desconectar");
            if (!this.getConnection().isClosed()) {
                this.getConnection().close();
            }
        } catch (SQLException e) {
            mensagem("Erro: " + e.getMessage());
        } finally {
            mensagem("Desconectado");
        }
    }

    public String mensagem(String texto) {
        String ret = texto;
        System.out.println(ret);
        return ret;
    }
}
