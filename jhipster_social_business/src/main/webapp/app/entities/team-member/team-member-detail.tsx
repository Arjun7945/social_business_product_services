import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './team-member.reducer';

export const TeamMemberDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const teamMemberEntity = useAppSelector(state => state.teamMember.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="teamMemberDetailsHeading">
          <Translate contentKey="whatsappProductServiceProApp.teamMember.detail.title">TeamMember</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{teamMemberEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="whatsappProductServiceProApp.teamMember.name">Name</Translate>
            </span>
          </dt>
          <dd>{teamMemberEntity.name}</dd>
          <dt>
            <span id="waPhoneNumber">
              <Translate contentKey="whatsappProductServiceProApp.teamMember.waPhoneNumber">Wa Phone Number</Translate>
            </span>
          </dt>
          <dd>{teamMemberEntity.waPhoneNumber}</dd>
          <dt>
            <span id="phoneNumber">
              <Translate contentKey="whatsappProductServiceProApp.teamMember.phoneNumber">Phone Number</Translate>
            </span>
          </dt>
          <dd>{teamMemberEntity.phoneNumber}</dd>
          <dt>
            <span id="role">
              <Translate contentKey="whatsappProductServiceProApp.teamMember.role">Role</Translate>
            </span>
          </dt>
          <dd>{teamMemberEntity.role}</dd>
          <dt>
            <span id="isActive">
              <Translate contentKey="whatsappProductServiceProApp.teamMember.isActive">Is Active</Translate>
            </span>
          </dt>
          <dd>{teamMemberEntity.isActive ? 'true' : 'false'}</dd>
        </dl>
        <Button tag={Link} to="/team-member" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/team-member/${teamMemberEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TeamMemberDetail;
