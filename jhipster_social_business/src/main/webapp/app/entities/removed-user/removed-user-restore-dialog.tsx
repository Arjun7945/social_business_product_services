import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntity, restoreEntity } from './removed-user.reducer';

export const RemovedUserRestoreDialog = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id));
    setLoadModal(true);
  }, []);

  const removedUserEntity = useAppSelector(state => state.removedUser.entity);
  const updateSuccess = useAppSelector(state => state.removedUser.updateSuccess);

  const handleClose = () => {
    navigate(`/removed-user${pageLocation.search}`);
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmRestore = () => {
    dispatch(restoreEntity(removedUserEntity.id));
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="removedUserRestoreDialogHeading">
        <Translate contentKey="entity.restore.title">Confirm restore operation</Translate>
      </ModalHeader>
      <ModalBody id="whatsappProductServiceProApp.removedUser.restore.question">
        <Translate contentKey="whatsappProductServiceProApp.removedUser.restore.question" interpolate={{ id: removedUserEntity.id }}>
          Are you sure you want to restore this RemovedUser?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button id="jhi-confirm-restore-removedUser" data-cy="entityConfirmRestoreButton" color="success" onClick={confirmRestore}>
          <FontAwesomeIcon icon="trash-restore" />
          &nbsp;
          <Translate contentKey="entity.action.restore">Restore</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default RemovedUserRestoreDialog;
