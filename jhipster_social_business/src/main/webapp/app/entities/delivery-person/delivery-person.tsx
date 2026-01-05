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

import { getEntities } from './delivery-person.reducer';

export const DeliveryPerson = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const deliveryPersonList = useAppSelector(state => state.deliveryPerson.entities);
  const loading = useAppSelector(state => state.deliveryPerson.loading);
  const totalItems = useAppSelector(state => state.deliveryPerson.totalItems);

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
      <h2 id="delivery-person-heading" data-cy="DeliveryPersonHeading">
        <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.home.title">Delivery People</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/delivery-person/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.home.createLabel">Create new Delivery Person</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {deliveryPersonList && deliveryPersonList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('name')}>
                  <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.name">Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                </th>
                <th className="hand" onClick={sort('waPhoneNumber')}>
                  <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.waPhoneNumber">Wa Phone Number</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('waPhoneNumber')} />
                </th>
                <th className="hand" onClick={sort('phoneNumber')}>
                  <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.phoneNumber">Phone Number</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('phoneNumber')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('joinedAt')}>
                  <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.joinedAt">Joined At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('joinedAt')} />
                </th>
                <th className="hand" onClick={sort('isActive')}>
                  <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.isActive">Is Active</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('isActive')} />
                </th>
                <th>
                  <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.addedBy">Added By</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.zone">Zone</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('chosenOrderLimit')}>
                  <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.chosenOrderLimit">Limit</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('chosenOrderLimit')} />
                </th>
                <th className="hand" onClick={sort('chosenOrder')}>
                  <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.chosenOrder">Chosen Orders</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('chosenOrder')} />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {deliveryPersonList.map((deliveryPerson, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/delivery-person/${deliveryPerson.id}`} color="link" size="sm">
                      {deliveryPerson.id}
                    </Button>
                  </td>
                  <td>{deliveryPerson.name}</td>
                  <td>{deliveryPerson.waPhoneNumber}</td>
                  <td>{deliveryPerson.phoneNumber}</td>
                  <td>
                    <Translate contentKey={`whatsappProductServiceProApp.DeliveryStatus.${deliveryPerson.status}`} />
                  </td>
                  <td>
                    {deliveryPerson.joinedAt ? <TextFormat type="date" value={deliveryPerson.joinedAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>{deliveryPerson.isActive ? 'true' : 'false'}</td>
                  <td>
                    {deliveryPerson.addedBy ? (
                      <Link to={`/team-member/${deliveryPerson.addedBy.id}`}>{deliveryPerson.addedBy.name}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {deliveryPerson.zone ? <Link to={`/delivery-zone/${deliveryPerson.zone.id}`}>{deliveryPerson.zone.zoneName}</Link> : ''}
                  </td>
                  <td>{deliveryPerson.chosenOrderLimit}</td>
                  <td>
                    {deliveryPerson.chosenOrder
                      ? deliveryPerson.chosenOrder.split(',').map((orderId, idx) => (
                          <span key={idx}>
                            <Link to={`/customer-order/${orderId}`}>{orderId}</Link>
                            {idx < deliveryPerson.chosenOrder.split(',').length - 1 ? ', ' : ''}
                          </span>
                        ))
                      : null}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/delivery-person/${deliveryPerson.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/delivery-person/${deliveryPerson.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/delivery-person/${deliveryPerson.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="whatsappProductServiceProApp.deliveryPerson.home.notFound">No Delivery People found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={deliveryPersonList && deliveryPersonList.length > 0 ? '' : 'd-none'}>
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

export default DeliveryPerson;
