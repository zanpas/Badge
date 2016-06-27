package it.sintegra.badge.business.mybatis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

/**
 * Classe per recuperare la sessione da Mybatis
 * 
 * @author Zannino Pasquale
 * */
public class MyBatisConnectionFactory {

	private static final Logger logger = Logger.getLogger(MyBatisConnectionFactory.class);

	private static SqlSessionFactory sqlSessionFactory;

	static {
		try {
			String resource = "mybatis/SqlMapConfig.xml";
			Reader reader = Resources.getResourceAsReader(resource);

			if (sqlSessionFactory == null) {
				sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
				logger.info("Connessione al db effettuata");
			}
		} catch (FileNotFoundException fileNotFoundException) {
			fileNotFoundException.printStackTrace();
		} catch (IOException iOException) {
			iOException.printStackTrace();
		}
	}

	public static SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

}
