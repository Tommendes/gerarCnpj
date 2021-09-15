SELECT COUNT(cnae_fiscal) FROM empresas WHERE
(email LIKE '%@gmail%' OR email LIKE '%@hotmail%' OR email LIKE '%@yahoo%' OR email LIKE '%@outlook%'
OR email LIKE '%@uol%' OR email LIKE '%@ig%' OR email LIKE '%@bol%' OR email LIKE '%@globo%' OR email LIKE '%@oi%')
GROUP BY cnae_fiscal
ORDER BY COUNT(cnae_fiscal) DESC;

SELECT COUNT(email) FROM empresas WHERE email LIKE '%@%' AND (email LIKE '%@gmail%' OR email LIKE '%@hotmail%' OR email LIKE '%@yahoo%' OR email LIKE '%@outlook%'
OR email LIKE '%@uol%' OR email LIKE '%@ig%' OR email LIKE '%@bol%' OR email LIKE '%@globo%' OR email LIKE '%@oi%'); /*13804960*/

SELECT COUNT(email),`uf` FROM empresas WHERE cnae_fiscal LIKE '711%' AND (email LIKE '%@gmail%' OR email LIKE '%@hotmail%' OR email LIKE '%@yahoo%' OR email LIKE '%@outlook%'
OR email LIKE '%@uol%' OR email LIKE '%@ig%' OR email LIKE '%@bol%' OR email LIKE '%@globo%' OR email LIKE '%@oi%')
GROUP BY uf; /*56946*/

SELECT nome,telefone FROM cadastros WHERE SUBSTRING(telefone,6,1) = '9';

SELECT COUNT(*) FROM socios;/*26188771*/
SELECT COUNT(*) FROM empresas;/*41.513.197*/
SELECT COUNT(*) FROM cnaes_secundarios; /*50.860.212*/

