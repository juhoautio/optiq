/*
// Licensed to Julian Hyde under one or more contributor license
// agreements. See the NOTICE file distributed with this work for
// additional information regarding copyright ownership.
//
// Julian Hyde licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
*/
package net.hydromatic.optiq.jdbc;

import java.sql.SQLException;

/**
 * Called at various points in the JDBC lifecycle.
 *
 * <p>Most drivers will use {@link HandlerImpl}, which provides no-op
 * implementations of all methods. You only need to override methods if you
 * need to achieve special effects.</p>
 */
public interface Handler {
  /** Called by Optiq server when a connection is being created.
   *
   * <p>If the implementation of this method throws, the connection
   * will not be created.</p>
   *
   * @param connection Connection
   * @throws SQLException on error
   */
  void onConnectionInit(OptiqConnection connection) throws SQLException;

  /** Called by Optiq server when a connection is being closed.
   *
   * <p>If the implementation of this method throws, the call to
   * {@link java.sql.Connection#close} that triggered this method will throw an
   * exception, but the connection will still be marked closed.</p>
   *
   * @param connection Connection
   */
  void onConnectionClose(OptiqConnection connection) throws RuntimeException;

  /** Called by Optiq server when a statement is being executed.
   *
   * <p>If the session would like the statement results stored in a temporary
   * table, {@code resultSink} is not null.
   * The provider must call its {@link ResultSink#toBeCompleted}
   * method at some point during execution (not necessarily before the call to
   * this method returns).</p>
   *
   * @param statement Statement
   * @param resultSink Place to put result of query. Null if Optiq does not
   *                   want results stored to a temporary table
   * @throws RuntimeException on error
   */
  void onStatementExecute(
      OptiqStatement statement,
      ResultSink resultSink) throws RuntimeException;

  /** Called by Optiq server when a statement is being closed.
   *
   * <p>This method is called after marking the statement closed, and after
   * closing any open {@link java.sql.ResultSet} objects.</p>
   *
   * <p>If the implementation of this method throws, the call to
   * {@link java.sql.Statement#close} that triggered this method will throw an
   * exception, but the statement will still be marked closed.
   *
   * @param statement Statement
   * @throws RuntimeException on error
   */
  void onStatementClose(OptiqStatement statement) throws RuntimeException;

  interface ResultSink {
    /** Registers a temporary table. */
    void toBeCompleted();
  }
}

// End Handler.java
