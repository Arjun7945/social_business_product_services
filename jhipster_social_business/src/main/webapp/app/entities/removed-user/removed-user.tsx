import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './removed-user.reducer';

export const RemovedUser = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const removedUserList = useAppSelector(state => state.removedUser.entities);
  const loading = useAppSelector(state => state.removedUser.loading);
  const totalItems = useAppSelector(state => state.removedUser.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="removed-user-heading" data-cy="RemovedUserHeading">
        <Translate contentKey="whatsappProductServiceProApp.removedUser.home.title">Removed Users</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="whatsappProductServiceProApp.removedUser.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/removed-user/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="whatsappProductServiceProApp.removedUser.home.createLabel">Create new Removed User</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {removedUserList && removedUserList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedUser.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('originalId')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedUser.originalId">Original Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('originalId')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedUser.name">Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('role')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedUser.role">Role</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('role')} />
                </th>
                <th className="hand" onClick={sort('whatsappNumber')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedUser.whatsappNumber">Whatsapp Number</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('whatsappNumber')} />
                </th>
                <th className="hand" onClick={sort('phoneNumber')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedUser.phoneNumber">Phone Number</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('phoneNumber')} />
                </th>
                <th className="hand" onClick={sort('address')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedUser.address">Address</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('address')} />
                </th>
                <th className="hand" onClick={sort('locationLat')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedUser.locationLat">Location Lat</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('locationLat')} />
                </th>
                <th className="hand" onClick={sort('locationLon')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedUser.locationLon">Location Lon</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('locationLon')} />
                </th>
                <th className="hand" onClick={sort('joinedAt')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedUser.joinedAt">Joined At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('joinedAt')} />
                </th>
                <th className="hand" onClick={sort('removedAt')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedUser.removedAt">Removed At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('removedAt')} />
                </th>
                <th className="hand" onClick={sort('reasonForRemoval')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedUser.reasonForRemoval">Reason For Removal</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('reasonForRemoval')} />
                </th>
                <th className="hand" onClick={sort('lastSessionData')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedUser.lastSessionData">Last Session Data</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('lastSessionData')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedUser.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('orderHistoryId')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedUser.orderHistoryId">Order History Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('orderHistoryId')} />
                </th>
                <th className="hand" onClick={sort('distanceFromBusinessKm')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedUser.distanceFromBusinessKm">
                    Distance From Business Km
                  </Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('distanceFromBusinessKm')} />
                </th>
                <th className="hand" onClick={sort('isPincodeValid')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedUser.isPincodeValid">Is Pincode Valid</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isPincodeValid')} />
                </th>
                <th className="hand" onClick={sort('zoneName')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedUser.zoneName">Zone</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('zoneName')} />
                </th>
                <th className="hand" onClick={sort('addedBy')}>
                  <Translate contentKey="whatsappProductServiceProApp.removedUser.addedBy">Added By</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('addedBy')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {removedUserList.map((removedUser, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/removed-user/${removedUser.id}`} color="link" size="sm">
                      {removedUser.id}
                    </Button>
                  </td>
                  <td>{removedUser.originalId}</td>
                  <td>{removedUser.name}</td>
                  <td>
                    <Translate contentKey={`whatsappProductServiceProApp.UserRole.${removedUser.role}`} />
                  </td>
                  <td>{removedUser.whatsappNumber}</td>
                  <td>{removedUser.phoneNumber}</td>
                  <td>{removedUser.address}</td>
                  <td>{removedUser.locationLat}</td>
                  <td>{removedUser.locationLon}</td>
                  <td>{removedUser.joinedAt ? <TextFormat type="date" value={removedUser.joinedAt} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>
                    {removedUser.removedAt ? <TextFormat type="date" value={removedUser.removedAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>{removedUser.reasonForRemoval}</td>
                  <td>{removedUser.lastSessionData}</td>
                  <td>
                    <Translate contentKey={`whatsappProductServiceProApp.AccountStatus.${removedUser.status}`} />
                  </td>
                  <td>
                    {removedUser.orderHistoryId ? (
                      <Link to={`/removed-order-summary/${removedUser.orderHistoryId}`}>{removedUser.orderHistoryId}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>{removedUser.distanceFromBusinessKm}</td>
                  <td>{removedUser.isPincodeValid ? 'true' : 'false'}</td>
                  <td>{removedUser.zoneName}</td>
                  <td>{removedUser.addedBy}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/removed-user/${removedUser.id}/restore?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="success"
                        size="sm"
                        data-cy="entityRestoreButton"
                      >
                        <FontAwesomeIcon icon="trash-restore" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.restore">Restore</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/removed-user/${removedUser.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/removed-user/${removedUser.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/removed-user/${removedUser.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="whatsappProductServiceProApp.removedUser.home.notFound">No Removed Users found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={removedUserList && removedUserList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default RemovedUser;
