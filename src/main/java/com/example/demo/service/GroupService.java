package com.example.demo.service;

import com.example.demo.entity.Group;
import com.example.demo.repository.GroupRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupService {

  private final GroupRepository groupRepository;

  public Group save(Group group) {
    validateGroup(group);

    return groupRepository.save(group);
  }

  public Group findById(UUID id) {
    return groupRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("Group not found with id: " + id));
  }

  public List<Group> findAll() {
    return groupRepository.findAll();
  }

  public List<Group> findByPromotionId(UUID promotionId) {
    return groupRepository.findByPromotionId(promotionId);
  }

  public List<Group> findByPromotionIdOrderByReference(UUID promotionId) {
    return groupRepository.findByPromotionIdOrderByReferenceAsc(promotionId);
  }

  public Group update(UUID id, Group updatedGroup) {
    Group existing = findById(id);

    validateGroup(updatedGroup);

    existing.setReference(updatedGroup.getReference());
    existing.setPromotion(updatedGroup.getPromotion());

    return groupRepository.save(existing);
  }

  public void delete(UUID id) {
    Group group = findById(id);
    groupRepository.delete(group);
  }

  private void validateGroup(Group group) {
    if (group.getReference() == null || group.getReference().isBlank()) {
      throw new IllegalArgumentException("Group reference cannot be empty");
    }

    if (group.getPromotion() == null) {
      throw new IllegalArgumentException("Group promotion cannot be null");
    }
  }
}
