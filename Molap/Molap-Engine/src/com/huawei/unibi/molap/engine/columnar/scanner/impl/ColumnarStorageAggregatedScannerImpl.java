/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.huawei.unibi.molap.engine.columnar.scanner.impl;

import com.huawei.unibi.molap.engine.columnar.aggregator.impl.DataAggregator;
import com.huawei.unibi.molap.engine.columnar.aggregator.impl.MapBasedResultAggregatorImpl;
import com.huawei.unibi.molap.engine.columnar.keyvalue.AbstractColumnarScanResult;
import com.huawei.unibi.molap.engine.columnar.scanner.AbstractColumnarStorageScanner;
import com.huawei.unibi.molap.engine.schema.metadata.ColumnarStorageScannerInfo;

public class ColumnarStorageAggregatedScannerImpl extends AbstractColumnarStorageScanner
{

    public ColumnarStorageAggregatedScannerImpl(ColumnarStorageScannerInfo columnarStorageScannerInfo)
    {
        super(columnarStorageScannerInfo);
        this.columnarAggaregator = new MapBasedResultAggregatorImpl(
                columnarStorageScannerInfo.getColumnarAggregatorInfo(), new DataAggregator(
                        columnarStorageScannerInfo.isAutoAggregateTableRequest(),
                        columnarStorageScannerInfo.getColumnarAggregatorInfo()));
    }

    @Override
    public void scanStore()
    {
        while(leafIterator.hasNext())
        {
            blockDataHolder.setLeafDataBlock(leafIterator.next());
            addToQueryStats(blockDataHolder);
            blockDataHolder.reset();
            AbstractColumnarScanResult unProcessData = blockProcessor.getScannedData(blockDataHolder);
            this.columnarAggaregator.aggregateData(unProcessData);
        }
        finish();
    }
}