/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gerarcnpj;

import org.apache.commons.lang3.StringUtils;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author TomMe
 */
public class GerarCnpj {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        String cnpj;
        Integer count = 0;
        FileWriter arq = null;
        PrintWriter gravarArq = null;
        try {
            arq = new FileWriter("C:\\Users\\TomMe\\Desktop\\GeraCnpj.sql");
            gravarArq = new PrintWriter(arq);
            for (int i = 1; i <= 99; i++) {
                for (int x = 1; x <= 999; x++) {
                    for (int y = 1; y <= 999; y++) {
                        for (int z = 1; z <= 9999; z++) {
                            for (int w = 1; w <= 99; w++) {
                                cnpj = StringUtils.leftPad(String.valueOf(i), 2, "0")
                                        + StringUtils.leftPad(String.valueOf(x), 3, "0")
                                        + StringUtils.leftPad(String.valueOf(y), 3, "0")
                                        + StringUtils.leftPad(String.valueOf(z), 4, "0")
                                        + StringUtils.leftPad(String.valueOf(w), 2, "0");
                                if (ValidaCNPJ.isCNPJ(cnpj) == true) {
                                    if (count % 10000 == 0) {
                                        System.out.println(count + "|" + cnpj);
                                    }
                                    gravarArq.printf(StringUtils.leftPad(cnpj, 14, "0") + ";%n");
                                    count++;
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(GerarCnpj.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            System.out.println(count + " cnpj válidos");
            gravarArq.printf(count + " cnpj válidos" + ";");
            arq.close();
            System.exit(0);
        }
    }

}
