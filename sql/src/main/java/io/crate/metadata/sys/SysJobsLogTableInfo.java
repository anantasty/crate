/*
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */

package io.crate.metadata.sys;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.crate.action.sql.SessionContext;
import io.crate.analyze.WhereClause;
import io.crate.metadata.ColumnIdent;
import io.crate.metadata.Routing;
import io.crate.metadata.RoutingProvider;
import io.crate.metadata.RowContextCollectorExpression;
import io.crate.metadata.RowGranularity;
import io.crate.metadata.TableIdent;
import io.crate.metadata.expressions.RowCollectExpressionFactory;
import io.crate.metadata.table.ColumnRegistrar;
import io.crate.metadata.table.StaticTableInfo;
import io.crate.execution.expression.reference.sys.job.JobContextLog;
import io.crate.types.DataTypes;
import org.elasticsearch.cluster.ClusterState;

import java.util.List;

public class SysJobsLogTableInfo extends StaticTableInfo {

    public static final TableIdent IDENT = new TableIdent(SysSchemaInfo.NAME, "jobs_log");
    private static final List<ColumnIdent> PRIMARY_KEYS = ImmutableList.of(Columns.ID);

    public static class Columns {
        public static final ColumnIdent ID = new ColumnIdent("id");
        static final ColumnIdent USERNAME = new ColumnIdent("username");
        static final ColumnIdent STMT = new ColumnIdent("stmt");
        public static final ColumnIdent STARTED = new ColumnIdent("started");
        static final ColumnIdent ENDED = new ColumnIdent("ended");
        public static final ColumnIdent ERROR = new ColumnIdent("error");
    }

    public static ImmutableMap<ColumnIdent, RowCollectExpressionFactory<JobContextLog>> expressions() {
        return ImmutableMap.<ColumnIdent, RowCollectExpressionFactory<JobContextLog>>builder()
            .put(SysJobsLogTableInfo.Columns.ID,
                () -> RowContextCollectorExpression.objToBytesRef(JobContextLog::id))
            .put(SysJobsTableInfo.Columns.USERNAME,
                () -> RowContextCollectorExpression.objToBytesRef(JobContextLog::username))
            .put(SysJobsLogTableInfo.Columns.STMT,
                () -> RowContextCollectorExpression.objToBytesRef(JobContextLog::statement))
            .put(SysJobsLogTableInfo.Columns.STARTED,
                () -> RowContextCollectorExpression.forFunction(JobContextLog::started))
            .put(SysJobsLogTableInfo.Columns.ENDED,
                () -> RowContextCollectorExpression.forFunction(JobContextLog::ended))
            .put(SysJobsLogTableInfo.Columns.ERROR,
                () -> RowContextCollectorExpression.objToBytesRef(JobContextLog::errorMessage))
            .build();
    }

    SysJobsLogTableInfo() {
        super(IDENT, new ColumnRegistrar(IDENT, RowGranularity.DOC)
            .register(Columns.ID, DataTypes.STRING)
            .register(Columns.USERNAME, DataTypes.STRING)
            .register(Columns.STMT, DataTypes.STRING)
            .register(Columns.STARTED, DataTypes.TIMESTAMP)
            .register(Columns.ENDED, DataTypes.TIMESTAMP)
            .register(Columns.ERROR, DataTypes.STRING), PRIMARY_KEYS);
    }

    @Override
    public RowGranularity rowGranularity() {
        return RowGranularity.DOC;
    }

    @Override
    public Routing getRouting(ClusterState clusterState,
                              RoutingProvider routingProvider,
                              WhereClause whereClause,
                              RoutingProvider.ShardSelection shardSelection,
                              SessionContext sessionContext) {
        return Routing.forTableOnAllNodes(IDENT, clusterState.getNodes());
    }
}
