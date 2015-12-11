/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package safepoint.optimizations;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class SafepointKillLoopOptimizations {

    @Param({ "100", "1000", "10000" })
    public int size;

    private DataSet datasetA;
    private DataSet datasetB;

    private static final class DataSet {
        private final int[] data;

        public DataSet(DataSet ds) {
            this.data = Arrays.copyOf(ds.data, ds.data.length);
        }

        public DataSet(int size) {
            Random r = new Random();
            data = new int[size];
            for (int i = 0; i < size; ++i) {
                data[i] = r.nextInt();
            }
        }

        int intSize() {
            return data.length;
        }

        int intGet(int index) {
            return data[index];
        }

        void intSet(int index, int v) {
            data[index] = v;
        }

        long longSize() {
            return data.length;
        }

        int longGet(long index) {
            return data[(int) index];
        }

        void longSet(long index, int v) {
            data[(int) index] = v;
        }
    }

    @Setup(Level.Trial)
    public void setup() {
        datasetA = new DataSet(size);
        datasetB = new DataSet(datasetA);
    }

    @Benchmark
    public int SumInt() {
        int sum = 0;
        for (int index = 0; index < datasetA.intSize(); ++index) {
            sum += datasetA.intGet(index);
        }
        return sum;
    }

    @Benchmark
    public int sumLong() {
        int sum = 0;
        for (long index = 0; index < datasetA.longSize(); ++index) {
            sum += datasetA.longGet(index);
        }
        return sum;
    }

    @Benchmark
    public boolean equalsInt() {
        for (int index = 0; index < datasetA.intSize(); ++index) {
            if (datasetA.intGet(index) != datasetB.intGet(index))
                return false;
        }
        return true;
    }

    @Benchmark
    public boolean equalsLong() {
        for (long index = 0; index < datasetA.longSize(); ++index) {
            if (datasetA.longGet(index) != datasetB.longGet(index))
                return false;
        }
        return true;
    }

    @Benchmark
    public void fillInt() {
        for (int index = 0; index < datasetA.intSize(); ++index) {
            datasetA.intSet(index, size);
        }
    }

    @Benchmark
    public void fillLong() {
        for (long index = 0; index < datasetA.longSize(); ++index) {
            datasetA.longSet(index, size);
        }
    }

    @Benchmark
    public void copyInt() {
        for (int index = 0; index < datasetA.intSize(); ++index) {
            datasetA.intSet(index, datasetB.intGet(index));
        }
    }

    @Benchmark
    public void copyLong() {
        for (long index = 0; index < datasetA.longSize(); ++index) {
            datasetA.longSet(index, datasetB.longGet(index));
        }
    }

    @Benchmark
    public void copyLongWithBatches() {
        long baseIndex = 0;
        final long length = datasetA.longSize();
        final int copySize = 4096;
        for (; baseIndex < length; baseIndex += copySize) {
            copyIntLoop(baseIndex, copySize);
        }
        int leftover = (int) (length & 4095);
        copyIntLoop(baseIndex - 4096, leftover);
    }

    private void copyIntLoop(long baseIndex, final int copySize) {
        for (int offset = 0; offset < copySize; offset++) {
            datasetA.longSet(baseIndex + offset, datasetB.longGet(baseIndex + offset));
        }
    }
}