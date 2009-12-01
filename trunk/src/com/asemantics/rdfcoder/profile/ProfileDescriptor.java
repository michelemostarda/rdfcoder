/*
 * Copyright 2007-2008 Michele Mostarda ( michele.mostarda@gmail.com ).
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.asemantics.profile;

import com.asemantics.model.CoderFactory;


/**
 * A <i>profile descriptor</i> defines
 * language support features for RDFCoder.
 *
 */
public interface ProfileDescriptor {

    /**
     * Returns a human readable profile description.
     * 
     * @return a string containing a description.
     */
    String getProfileDescription();

    /**
     * Returns the {@link com.asemantics.model.CoderFactory}
     * for this profile.
     *
     * @return the factory instance.
     */
    CoderFactory getCoderFactory();
}
