package com.mslife.proposal.image.service;

import com.mslife.proposal.image.dto.User;

import java.io.IOException;

public interface ProposalService {

    String genPdfFromUser(User user) throws IOException;
}
