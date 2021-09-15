/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gerarcnpj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author TomMe
 */
public class Cnpj {

    public static ConexaoMy conexaoMy;

    private static String readAll(Reader rd) {
        try {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        } catch (IOException ex) {
            Logger.getLogger(Cnpj.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static JSONObject readJsonFromUrl(String url, String cnpj) {
        InputStream is = null;
        JSONObject json = null;
        try {
            is = new URL(url).openStream();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                json = new JSONObject(jsonText);
            } catch (JSONException ex) {
//                Logger.getLogger(Cnpj.class.getName()).log(Level.SEVERE, null, ex);
                insertCadastroInvalido(conexaoMy, cnpj);
            } finally {
                is.close();
            }
        } catch (MalformedURLException ex) {
//            Logger.getLogger(Cnpj.class.getName()).log(Level.SEVERE, null, ex);
            insertCadastroInvalido(conexaoMy, cnpj);
        } catch (IOException ex) {
//            Logger.getLogger(Cnpj.class.getName()).log(Level.SEVERE, null, ex);
            insertCadastroInvalido(conexaoMy, cnpj);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(Cnpj.class.getName()).log(Level.SEVERE, null, ex);
                System.exit(0);
            }
        }
        return json;
    }

    public static void main(String[] args) throws InterruptedException {
        String obj = "0";
        GeraCpfCnpj gerador = new GeraCpfCnpj();
        conexaoMy = new ConexaoMy();
        conexaoMy.conectar();
        String cnpj = null;
        Integer count = 1;
        if (0 == getTabelaContar(conexaoMy, "lista1", "")) {
            System.out.println("Lista vazia");
            for (int i = 1; i <= 60; i++) {
                cnpj = gerador.cnpj(false);//"08092231000184";//
                System.out.printf("%s|Verificar CNPJ %s\n", count, cnpj);
                String url = "https://www.receitaws.com.br/v1/cnpj/" + cnpj;
                if (0 == getTabelaContar(conexaoMy, "cadastros", "where cnpj = '" + cnpj + "'")) {
                    insertCadastros(conexaoMy, getData(url, cnpj), cnpj);
//                    Thread.sleep(21000);
                } else {
                    System.out.println("CNPJ " + cnpj + " já está registrado");
                }
                count++;
            }
        } else {
            System.out.println("Lista");
            ResultSet tabelaRecebe = getTabelaGenerico(conexaoMy, "lista1", "", "");
            try {
                while (tabelaRecebe.next()) {
                    cnpj = tabelaRecebe.getString("cnpj");
                    System.out.printf("%s|Verificar CNPJ %s\n", count, cnpj);
                    String url = "https://www.receitaws.com.br/v1/cnpj/" + cnpj;
                    if (0 == getTabelaContar(conexaoMy, "cadastros", "where cnpj = '" + cnpj + "'")) {
                        insertCadastros(conexaoMy, getData(url, cnpj), cnpj);
//                        Thread.sleep(21000);
                    } else {
                        System.out.println("CNPJ " + cnpj + " já está registrado");
                    }
                    setExecuteSQL(conexaoMy, "delete from lista1 where cnpj = '" + cnpj + "'");
                    count++;
                }
                tabelaRecebe.close();
            } catch (SQLException e) {
                System.out.println("Erro em main: " + e.getMessage());
            } finally {
            }
        }
    }

    public static JSONObject getData(String url, String cnpj) {
        JSONObject json = null;
        json = readJsonFromUrl(url, cnpj);
        return json;
    }

    /**
     * Insere os dados localizados no sistema
     *
     * @param connMy
     * @param json
     * @param cnpj
     * @return
     */
    public static boolean insertCadastros(ConexaoMy connMy, JSONObject json, String cnpj) {
        boolean resultado = false;
        String sql = "INSERT INTO `cadastros` (`id`,`cnpj`,`status`,`atividade_principal_code`,"
                + "`atividade_principal_text`,`data_situacao`,`nome`,`uf`,`telefone`,"
                + "`email`,`situacao`,`bairro`,`logradouro`,`numero`,`cep`,`municipio`,"
                + "`abertura`,`natureza_juridica`,`ultima_atualizacao`,`tipo`,`fantasia`,"
                + "`complemento`,`efr`,`motivo_situacao`,`situacao_especial`,"
                + "`data_situacao_especial`,`capital_social`,`created_at`, remetente)VALUES("
                + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,UNIX_TIMESTAMP(NOW()),?)";
        PreparedStatement ps1;
        try {
            try {
                Integer id = getTabelaId(connMy, "cadastros");
                ps1 = connMy.getConnection().prepareStatement(sql);
                ps1.setInt(1, id);
                ps1.setString(2, cnpj);
                ps1.setString(3, json.get("status").toString());
                JSONArray jArray = null;
                jArray = json.getJSONArray("atividade_principal");
                for (int x = 0; x < jArray.length(); x++) {
                    JSONObject ap = jArray.getJSONObject(x);
                    ps1.setString(4, ap.get("code").toString());
                    ps1.setString(5, ap.get("text").toString());
                }
                ps1.setString(6, json.get("data_situacao").toString());
                ps1.setString(7, json.get("nome").toString());
                ps1.setString(8, json.get("uf").toString());
                ps1.setString(9, json.get("telefone").toString());
                ps1.setString(10, json.get("email").toString());
                ps1.setString(11, json.get("situacao").toString());
                ps1.setString(12, json.get("bairro").toString());
                ps1.setString(13, json.get("logradouro").toString());
                ps1.setString(14, json.get("numero").toString());
                ps1.setString(15, json.get("cep").toString());
                ps1.setString(16, json.get("municipio").toString());
                ps1.setString(17, json.get("abertura").toString());
                ps1.setString(18, json.get("natureza_juridica").toString());
                ps1.setString(19, json.get("ultima_atualizacao").toString());
                ps1.setString(20, json.get("tipo").toString());
                ps1.setString(21, json.get("fantasia").toString());
                ps1.setString(22, json.get("complemento").toString());
                ps1.setString(23, json.get("efr").toString());
                ps1.setString(24, json.get("motivo_situacao").toString());
                ps1.setString(25, json.get("situacao_especial").toString());
                ps1.setString(26, json.get("data_situacao_especial").toString());
                ps1.setString(27, json.get("capital_social").toString());
                ps1.setString(28, System.getProperty("user.name"));
                ps1.executeUpdate();
                resultado = true;
                jArray = json.getJSONArray("atividades_secundarias");
                if (jArray.length() > 0) {
                    for (int x = 0; x < jArray.length(); x++) {
                        JSONObject ap = jArray.getJSONObject(x);
                        insertCadastrosAs(connMy, id, ap.get("code").toString(), ap.get("text").toString());
                    }
                }
                jArray = json.getJSONArray("qsa");
                if (jArray.length() > 0) {
                    for (int x = 0; x < jArray.length(); x++) {
                        JSONObject ap = jArray.getJSONObject(x);
                        insertCadastrosQsa(connMy, id, ap.get("qual").toString(), ap.get("nome").toString());
                    }
                }
                System.out.println("Cadastro :\"" + json.get("cnpj").toString() + "\" inserido");

            } catch (JSONException e) {
                Logger.getLogger(Cnpj.class
                        .getName()).log(Level.SEVERE, null, e);
                insertCadastroInvalido(connMy, cnpj);

            }
        } catch (SQLException e) {
            Logger.getLogger(Cnpj.class
                    .getName()).log(Level.SEVERE, null, e);
            insertCadastroInvalido(connMy, cnpj);
        }
        return resultado;
    }

    public static boolean insertCadastroInvalido(ConexaoMy connMy, String cnpj) {
        boolean resultado = false;
        String sql = "INSERT INTO `cadastros` (`id`,`cnpj`,`status`,`created_at`, remetente)VALUES("
                + "?,?,null,UNIX_TIMESTAMP(NOW()),?)";
        PreparedStatement ps1;
        try {
            Integer id = getTabelaId(connMy, "cadastros");
            ps1 = connMy.getConnection().prepareStatement(sql);
            ps1.setInt(1, id);
            ps1.setString(2, cnpj);
            ps1.setString(3, System.getProperty("user.name"));
            ps1.executeUpdate();
            resultado = true;
            System.out.println("Cadastro inválido:\"" + cnpj + "\" inserido");

        } catch (SQLException e) {
            Logger.getLogger(Cnpj.class
                    .getName()).log(Level.SEVERE, null, e);
        }
        return resultado;
    }

    /**
     * Insere os dados de atividade secundária localizados no sistema
     *
     * @param connMy
     * @param id_cadastros
     * @param code
     * @param text
     */
    public static void insertCadastrosAs(ConexaoMy connMy, Integer id_cadastros, String code, String text) {
        String sql = "INSERT INTO `cadastros_as` (`id_cadastros`,`code`,`text`,`created_at`)VALUES("
                + "?,?,?,UNIX_TIMESTAMP(NOW()),?)";
        PreparedStatement ps1;
        try {
            ps1 = connMy.getConnection().prepareStatement(sql);
            ps1.setInt(1, id_cadastros);
            ps1.setString(2, code);
            ps1.setString(3, text);
            ps1.executeUpdate();
            System.out.println("Atividade secundária :\"" + code + "\" inserida");

        } catch (SQLException ex) {
            Logger.getLogger(Cnpj.class
                    .getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    /**
     * Insere os dados de atividade secundária localizados no sistema
     *
     * @param connMy
     * @param id_cadastros
     * @param qual
     * @param nome
     */
    public static void insertCadastrosQsa(ConexaoMy connMy, Integer id_cadastros, String qual, String nome) {
        String sql = "INSERT INTO `cadastros_qsa` (`id_cadastros`,`qual`,`nome`,`created_at`)VALUES("
                + "?,?,?,UNIX_TIMESTAMP(NOW()))";
        PreparedStatement ps1;
        try {
            ps1 = connMy.getConnection().prepareStatement(sql);
            ps1.setInt(1, id_cadastros);
            ps1.setString(2, qual);
            ps1.setString(3, nome);
            ps1.executeUpdate();
            System.out.println("Qsa :\"" + nome + "\" inserido");

        } catch (SQLException ex) {
            Logger.getLogger(Cnpj.class
                    .getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    /**
     * Conta os registros da tabela referenciada no parametro tabela no
     * construtor
     *
     * @param conn
     * @param tabela
     * @param sqlAdd
     * @return
     */
    public static Integer getTabelaContar(ConexaoMy conn, String tabela, String sqlAdd) {
        Integer quant = 0;
        String sql = "select count(*) as quant from " + tabela + " " + sqlAdd;
        try (
                Statement stmtcontar = conn.getConnection().createStatement();
                ResultSet quantidade = stmtcontar.executeQuery(sql)) {
            if (quantidade.next()) {
                quant = quantidade.getInt("quant");
            }
        } catch (SQLException e) {
            System.out.println("Houve um erro ao contar os registros na tabela. Erro: " + e.getMessage());
            System.exit(0);
        } finally {
            return quant;
        }
    }

    public static Integer getTabelaId(ConexaoMy conn, String tabela) {
        Integer quant = 0;
        String sql = "SELECT MAX(id) + 1 AS quant FROM " + tabela;
        try (
                Statement stmtcontar = conn.getConnection().createStatement();
                ResultSet quantidade = stmtcontar.executeQuery(sql)) {
            if (quantidade.next()) {
                quant = quantidade.getInt("quant");
            }
        } catch (SQLException e) {
            System.out.println("Houve um erro ao contar os registros na tabela. Erro: " + e.getMessage());
            System.exit(0);
        } finally {
            return quant;
        }
    }

    /**
     * Recupera os registros da tabela referenciada no parametro tabela no
     * construtor
     *
     * @param conn
     * @param tabela
     * @param sqlAdd
     * @param salto
     * @return
     */
    public static ResultSet getTabelaGenerico(ConexaoMy conn, String tabela, String sqlAdd, String salto) {
        ResultSet rs;
        try {
            String sql = "SELECT " + salto + " * from " + tabela + " " + sqlAdd;
            System.out.println("SQL: " + sql);
            Statement stmt = conn.getConnection().createStatement();
            rs = stmt.executeQuery(sql);
            return rs;
        } catch (SQLException e) {
            System.out.println("Houve um erro ao ler a tabela " + tabela + ". Erro: " + e.getMessage());
            return null;
        }
    }

    /**
     * Executa um determinado comando sql genérico
     *
     * @param conn
     * @param sql
     * @return
     */
    public static boolean setExecuteSQL(ConexaoMy conn, String sql) {
        try {
            System.out.println("SQL: " + sql);
            PreparedStatement ps1 = conn.getConnection().prepareStatement(sql);
            ps1.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Houve um erro ao atualizar o comando. Erro: " + e.getMessage());
            return false;
        }
    }

}
