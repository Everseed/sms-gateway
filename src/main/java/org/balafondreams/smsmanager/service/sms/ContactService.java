package org.balafondreams.smsmanager.service.sms;


import lombok.RequiredArgsConstructor;
import org.balafondreams.smsmanager.domain.entities.sms.Contact;
import org.balafondreams.smsmanager.domain.entities.sms.Group;
import org.balafondreams.smsmanager.domain.exception.DuplicateResourceException;
import org.balafondreams.smsmanager.domain.exception.ResourceNotFoundException;
import org.balafondreams.smsmanager.domain.mapper.ContactMapper;
import org.balafondreams.smsmanager.domain.models.sms.ContactCreateDTO;
import org.balafondreams.smsmanager.domain.models.sms.ContactDTO;
import org.balafondreams.smsmanager.repository.ContactRepository;
import org.balafondreams.smsmanager.repository.GroupRepository;
import org.balafondreams.smsmanager.repository.UserRepository;
import org.balafondreams.smsmanager.service.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ContactService {
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ContactMapper contactMapper;
    private final UserService userService;

    /**
     * Récupère tous les contacts d'un utilisateur
     */
    public Page<ContactDTO> getUserContacts(Long userId, Pageable pageable) {
        return contactRepository.findByUserId(userId, pageable)
                .map(contactMapper::toDto);
    }

    /**
     * Crée un nouveau contact
     */
    public ContactDTO createContact(ContactCreateDTO createDTO, Long userId) {
        // Vérifier si le contact existe déjà pour cet utilisateur
        if (contactRepository.existsByPhoneNumberAndUserId(
                createDTO.getPhoneNumber(), userId)) {
            throw new DuplicateResourceException(
                    "Contact with this phone number already exists"
            );
        }

        Contact contact = contactMapper.toEntity(createDTO);
        contact.setUser(userService.getCurrentUser());

        Contact saved = contactRepository.save(contact);
        return contactMapper.toDto(saved);
    }

    /**
     * Met à jour un contact existant
     */
    public ContactDTO updateContact(Long id, ContactDTO updateDTO, Long userId) {
        Contact contact = contactRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        // Vérifier si le nouveau numéro n'existe pas déjà
        if (!contact.getPhoneNumber().equals(updateDTO.getPhoneNumber()) &&
                contactRepository.existsByPhoneNumberAndUserId(
                        updateDTO.getPhoneNumber(), userId)) {
            throw new DuplicateResourceException(
                    "Contact with this phone number already exists"
            );
        }

        contactMapper.updateEntity(updateDTO, contact);
        Contact updated = contactRepository.save(contact);
        return contactMapper.toDto(updated);
    }

    /**
     * Supprime un contact
     */
    public void deleteContact(Long id, Long userId) {
        Contact contact = contactRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
        contactRepository.delete(contact);
    }

    /**
     * Recherche de contacts
     */
    public Page<ContactDTO> searchContacts(String query, Long userId, Pageable pageable) {
        return contactRepository.searchContacts(query, userId, pageable)
                .map(contactMapper::toDto);
    }

    /**
     * Récupère un contact par ID
     */
    public ContactDTO getContact(Long id, Long userId) {
        return contactRepository.findByIdAndUserId(id, userId)
                .map(contactMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
    }

    /**
     * Vérifie si un numéro existe déjà pour un utilisateur
     */
    public boolean isPhoneNumberExists(String phoneNumber, Long userId) {
        return contactRepository.existsByPhoneNumberAndUserId(phoneNumber, userId);
    }

    public ContactDTO addToGroups(Long contactId, Set<Long> groupIds, Long userId) {
        Contact contact = contactRepository.findByIdAndUserId(contactId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        Set<Group> groups = groupRepository.findAllById(groupIds)
                .stream()
                .filter(group -> group.getUser().getId().equals(userId))
                .collect(Collectors.toSet());

        groups.forEach(group -> {
            group.getContacts().add(contact);
            contact.getGroups().add(group);
        });

        Contact updated = contactRepository.save(contact);
        return contactMapper.toDto(updated);
    }

    public ContactDTO removeFromGroup(Long contactId, Long groupId, Long userId) {
        Contact contact = contactRepository.findByIdAndUserId(contactId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));

        Group group = groupRepository.findByIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        contact.getGroups().remove(group);
        group.getContacts().remove(contact);

        Contact updated = contactRepository.save(contact);
        return contactMapper.toDto(updated);
    }
}