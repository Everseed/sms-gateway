package org.balafondreams.smsmanager.service.sms;

import lombok.RequiredArgsConstructor;
import org.balafondreams.smsmanager.domain.entities.sms.Contact;
import org.balafondreams.smsmanager.domain.entities.sms.Group;
import org.balafondreams.smsmanager.domain.exception.DuplicateResourceException;
import org.balafondreams.smsmanager.domain.exception.ResourceNotFoundException;
import org.balafondreams.smsmanager.domain.mapper.GroupMapper;
import org.balafondreams.smsmanager.domain.models.sms.GroupCreateDTO;
import org.balafondreams.smsmanager.domain.models.sms.GroupDTO;
import org.balafondreams.smsmanager.domain.models.sms.GroupSummaryDTO;
import org.balafondreams.smsmanager.repository.ContactRepository;
import org.balafondreams.smsmanager.repository.GroupRepository;
import org.balafondreams.smsmanager.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupService {
    private final GroupRepository groupRepository;
    private final ContactRepository contactRepository;
    private final GroupMapper groupMapper;
    private final UserService userService;

    public GroupDTO createGroup(GroupCreateDTO createDTO, Long userId) {
        // Vérifier si le nom est déjà utilisé
        if (groupRepository.existsByNameAndUserId(createDTO.getName(), userId)) {
            throw new DuplicateResourceException("Group name already exists");
        }

        Group group = groupMapper.toEntity(createDTO);
        group.setUser(userService.getCurrentUser());

        if (createDTO.getContactIds() != null && !createDTO.getContactIds().isEmpty()) {
            Set<Contact> contacts = contactRepository.findAllById(createDTO.getContactIds())
                    .stream()
                    .filter(contact -> contact.getUser().getId().equals(userId))
                    .collect(Collectors.toSet());
            group.setContacts(contacts);
        }

        Group saved = groupRepository.save(group);
        return groupMapper.toDto(saved);
    }

    public GroupDTO addContactsToGroup(Long groupId, Set<Long> contactIds, Long userId) {
        Group group = groupRepository.findByIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        Set<Contact> contacts = contactRepository.findAllById(contactIds)
                .stream()
                .filter(contact -> contact.getUser().getId().equals(userId))
                .collect(Collectors.toSet());

        group.getContacts().addAll(contacts);
        Group updated = groupRepository.save(group);
        return groupMapper.toDto(updated);
    }

    public void removeContactFromGroup(Long groupId, Long contactId, Long userId) {
        Group group = groupRepository.findByIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        group.getContacts().removeIf(contact -> contact.getId().equals(contactId));
        groupRepository.save(group);
    }

    public Page<GroupSummaryDTO> getUserGroups(Long userId, Pageable pageable) {
        return groupRepository.findByUserId(userId, pageable)
                .map(groupMapper::toSummaryDto);
    }

    public GroupDTO getGroupDetails(Long groupId, Long userId) {
        Group group = groupRepository.findByIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));
        return groupMapper.toDto(group);
    }
}
