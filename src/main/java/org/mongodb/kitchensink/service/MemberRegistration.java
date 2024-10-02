/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mongodb.kitchensink.service;


import jakarta.inject.Inject;
import org.mongodb.kitchensink.data.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;
import org.mongodb.kitchensink.model.Member;
import jakarta.enterprise.event.Event;
import org.springframework.transaction.annotation.Transactional;

// The @Stateless annotation eliminates the need for manual transaction demarcation
@Service
@Transactional
public class MemberRegistration {

    private static final Logger logger = Logger.getLogger(MemberRegistration.class.getName());

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    MemberService memberService;

    public void register(Member member) throws Exception {
        logger.info("Registering " + member.getName());
        memberService.createMember(member);
        if(this.eventProducer != null)
            eventProducer.publish(member);
    }
}
