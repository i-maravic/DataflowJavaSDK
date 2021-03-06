/*
 * Copyright (C) 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.dataflow.sdk.util.state;

import com.google.cloud.dataflow.sdk.coders.Coder;
import com.google.cloud.dataflow.sdk.transforms.Combine.CombineFn;
import com.google.cloud.dataflow.sdk.util.state.StateTag.StateBinder;

import org.joda.time.Instant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * In-memory implementation of {@link StateInternals}. Used in {@code BatchModeExecutionContext}
 * and for running tests that need state.
 */
public class InMemoryStateInternals extends MergingStateInternals {

  private final StateTable inMemoryState = new StateTable() {
    @Override
    protected StateBinder binderForNamespace(final StateNamespace namespace) {
      return new StateBinder() {

        @Override
        public <T> ValueState<T> bindValue(StateTag<ValueState<T>> address, Coder<T> coder) {
          return new InMemoryValue<T>();
        }

        @Override
        public <T> BagState<T> bindBag(final StateTag<BagState<T>> address, Coder<T> elemCoder) {
          return new InMemoryBag<T>();
        }

        @Override
        public <InputT, AccumT, OutputT>
        CombiningValueStateInternal<InputT, AccumT, OutputT> bindCombiningValue(
            StateTag<CombiningValueStateInternal<InputT, AccumT, OutputT>> address,
            Coder<AccumT> accumCoder,
            final CombineFn<InputT, AccumT, OutputT> combineFn) {
          return new InMemoryCombiningValue<InputT, AccumT, OutputT>(combineFn);
        }

        @Override
        public <T> WatermarkStateInternal bindWatermark(StateTag<WatermarkStateInternal> address) {
          return new WatermarkBagInternalImplementation();
        }
      };
    }
  };

  @Override
  public <T extends State> T state(StateNamespace namespace, StateTag<T> address) {
    return inMemoryState.get(namespace, address);
  }

  private final class InMemoryValue<T> implements ValueState<T> {
    private T value = null;

    @Override
    public void clear() {
      // Even though we're clearing we can't remove this from the in-memory state map, since
      // other users may already have a handle on this Value.
      value = null;
    }

    @Override
    public StateContents<T> get() {
      return new StateContents<T>() {
        @Override
        public T read() {
          return value;
        }
      };
    }

    @Override
    public void set(T input) {
      this.value = input;
    }
  }

  private final class WatermarkBagInternalImplementation implements WatermarkStateInternal {
    private Instant minimumHold = null;

    @Override
    public void clear() {
      // Even though we're clearing we can't remove this from the in-memory state map, since
      // other users may already have a handle on this WatermarkBagInteranl.
      minimumHold = null;
    }

    @Override
    public StateContents<Instant> get() {
      return new StateContents<Instant>() {
        @Override
        public Instant read() {
          return minimumHold;
        }
      };
    }

    @Override
    public void add(Instant watermarkHold) {
      if (minimumHold == null || minimumHold.isAfter(watermarkHold)) {
        minimumHold = watermarkHold;
      }
    }
  }

  private final class InMemoryCombiningValue<InputT, AccumT, OutputT>
      implements CombiningValueStateInternal<InputT, AccumT, OutputT> {

    private final CombineFn<InputT, AccumT, OutputT> combineFn;
    private AccumT accum;

    private InMemoryCombiningValue(CombineFn<InputT, AccumT, OutputT> combineFn) {
      this.combineFn = combineFn;
      accum = combineFn.createAccumulator();
    }

    @Override
    public void clear() {
      // Even though we're clearing we can't remove this from the in-memory state map, since
      // other users may already have a handle on this CombiningValue.
      accum = combineFn.createAccumulator();
    }

    @Override
    public StateContents<OutputT> get() {
      return new StateContents<OutputT>() {
        @Override
        public OutputT read() {
          return combineFn.extractOutput(accum);
        }
      };
    }

    @Override
    public void add(InputT input) {
      accum = combineFn.addInput(accum, input);
    }

    @Override
    public StateContents<AccumT> getAccum() {
      return new StateContents<AccumT>() {
        @Override
        public AccumT read() {
          return accum;
        }
      };

    }

    @Override
    public void addAccum(AccumT accum) {
      this.accum = combineFn.mergeAccumulators(Arrays.asList(this.accum, accum));
    }
  }

  private static final class InMemoryBag<T> implements BagState<T> {
    private final List<T> contents = new ArrayList<>();

    @Override
    public void clear() {
      // Even though we're clearing we can't remove this from the in-memory state map, since
      // other users may already have a handle on this Bag.
      contents.clear();
    }

    @Override
    public StateContents<Iterable<T>> get() {
      return new StateContents<Iterable<T>>() {
        @Override
        public Iterable<T> read() {
          return contents;
        }
      };
    }

    @Override
    public void add(T input) {
      contents.add(input);
    }
  }
}
