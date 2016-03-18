package com.rockspoon.services;

import android.content.Context;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.rockspoon.models.session.ImmutableSession;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.sql.SQLException;

/**
 * Created by lucas on 14/01/16.
 */
@EBean(scope = EBean.Scope.Singleton)
public class SessionDatabaseService {

  @RootContext
  Context context;

  protected ConnectionSource connection;

  SessionDatabaseService() {
  }

  @AfterInject
  void afterInject() {
    connection = new AndroidConnectionSource(context.openOrCreateDatabase("deviceSession", Context.MODE_PRIVATE, null));
  }

  public void saveOrUpdateSession(final ImmutableSession session) throws SQLException {
    try {
      final Dao<ImmutableSession, String> dao = DaoManager.createDao(connection, ImmutableSession.class);

      if (!dao.isTableExists())
        TableUtils.createTable(connection, ImmutableSession.class);

      dao.createOrUpdate(session);
    } finally {
      connection.close();
    }
  }

  public ImmutableSession restoreSession(final String type) throws SQLException {
    try {
      final Dao<ImmutableSession, String> dao = DaoManager.createDao(connection, ImmutableSession.class);

      if (!dao.isTableExists())
        return null;

      return dao.queryForId(type);
    } finally {
      connection.close();
    }
  }


  public void removeSession(final ImmutableSession session) throws SQLException {
    try {
      final Dao<ImmutableSession, String> dao = DaoManager.createDao(connection, ImmutableSession.class);

      if (session != null) {
        if (dao.isTableExists())
          dao.delete(session);
      }
    } finally {
      connection.close();
    }
  }

}
